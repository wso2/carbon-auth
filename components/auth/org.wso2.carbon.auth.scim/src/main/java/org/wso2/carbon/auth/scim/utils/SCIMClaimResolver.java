/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.auth.scim.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.SCIMObject;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.AttributeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to resolve scim attributes
 *
 */
public class SCIMClaimResolver {
    
    private static Logger log = LoggerFactory.getLogger(SCIMClaimResolver.class);
    private static final boolean debug = log.isDebugEnabled();

    /*
     * Return claims as a map of <ClaimUri (which is mapped to SCIM attribute uri),ClaimValue>
     *
     * @param scimObject
     * @return
     */
    public static Map<String, String> getClaimsMap(AbstractSCIMObject scimObject) throws CharonException {
        Map<String, String> claimsMap = new HashMap<>();
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (Map.Entry<String, Attribute> attributeEntry : attributeList.entrySet()) {
            //we are treating password, group members and user groups attributes separately
            if (attributeEntry.getKey().equals(SCIMConstants.GroupSchemaConstants.MEMBERS)) {
                continue;
            } else if (attributeEntry.getKey().equals(SCIMConstants.UserSchemaConstants.GROUPS)) {
                continue;
            } else if (attributeEntry.getKey().equals(SCIMConstants.UserSchemaConstants.PASSWORD)) {
                continue;
            }

            Attribute attribute = attributeEntry.getValue();

            if (attribute instanceof SimpleAttribute) {
                setClaimsForSimpleAttribute(attribute, claimsMap);

            } else if (attribute instanceof MultiValuedAttribute) {
                setClaimsForMultivaluedAttribute(attribute, claimsMap);
            } else if (attribute instanceof ComplexAttribute) {
                // NOTE: in carbon, we only support storing of type and value of a complex multi-valued attribute
                // reading attributes list of the complex attribute
                ComplexAttribute complexAttribute = (ComplexAttribute) attribute;
                Map<String, Attribute> attributes = null;
                if (complexAttribute.getSubAttributesList() != null &&
                        !complexAttribute.getSubAttributesList().isEmpty()) {
                    attributes = complexAttribute.getSubAttributesList();
                }
                if (attributes != null) {
                    for (Attribute entry : attributes.values()) {
                        // if the attribute a simple attribute
                        if (entry instanceof SimpleAttribute) {
                            //we treat meta location attribute separately.
                            if (entry.getURI().equals(SCIMConstants.CommonSchemaConstants.LOCATION_URI)) {
                                continue;
                            }
                            setClaimsForSimpleAttribute(entry, claimsMap);

                        } else if (entry instanceof MultiValuedAttribute) {
                            setClaimsForMultivaluedAttribute(entry, claimsMap);

                        } else if (entry instanceof ComplexAttribute) {
                            setClaimsForComplexAttribute(entry, claimsMap);
                        }
                    }
                }
            }
        }
        return claimsMap;
    }

    /*
     * set claim mapping for simple attribute
     *
     * @param attribute
     * @param claimsMap
     */
    private static void setClaimsForSimpleAttribute(Attribute attribute, Map<String, String> claimsMap) throws
            CharonException {
        String attributeURI = attribute.getURI();
        if (((SimpleAttribute) attribute).getValue() != null) {
            String attributeValue = AttributeUtil.getStringValueOfAttribute(
                    ((SimpleAttribute) attribute).getValue(), attribute.getType());
            // set attribute URI as the claim URI
            claimsMap.put(attributeURI, attributeValue);
        }
    }

    /*
     * set claim mapping for multivalued attribute
     *
     * @param attribute
     * @param claimsMap
     */
    private static void setClaimsForMultivaluedAttribute(Attribute attribute, Map<String, String> claimsMap) throws
            CharonException {
        MultiValuedAttribute multiValAttribute = (MultiValuedAttribute) attribute;
        // get the URI of root attribute
        String attributeURI = multiValAttribute.getURI();
        // check if values are set as primitive values
        List<Object> attributeValues = multiValAttribute.getAttributePrimitiveValues();
        if (attributeValues != null && !attributeValues.isEmpty()) {
            String values = null;
            for (Object attributeValue : attributeValues) {
                if (values != null) {
                    values += attributeValue + ",";
                } else {
                    values = attributeValue + ",";
                }
            }
            claimsMap.put(attributeURI, values);
        }

        // check if values are set as complex values
        // NOTE: in carbon, we only support storing of type and
        // value of a multi-valued attribute
        List<Attribute> complexAttributeList = multiValAttribute.getAttributeValues();
        for (Attribute complexAttrib : complexAttributeList) {
            Map<String, Attribute> subAttributes =
                    ((ComplexAttribute) complexAttrib).getSubAttributesList();
            SimpleAttribute typeAttribute =
                    (SimpleAttribute) subAttributes.get(SCIMConstants.CommonSchemaConstants.TYPE);
            String valueAttriubuteURI;
            // construct attribute URI
            if (typeAttribute != null) {
                String typeValue = (String) typeAttribute.getValue();
                valueAttriubuteURI = attributeURI + "." + typeValue;
            } else {
                valueAttriubuteURI = attributeURI;
            }
            SimpleAttribute valueAttribute = null;
            if (attribute.getName().equals(SCIMConstants.UserSchemaConstants.ADDRESSES)) {
                valueAttribute =
                        (SimpleAttribute) subAttributes.get(SCIMConstants.UserSchemaConstants.FORMATTED_ADDRESS);
            } else {
                valueAttribute =
                        (SimpleAttribute) subAttributes.get(SCIMConstants.CommonSchemaConstants.VALUE);
            }
            if (valueAttribute != null && valueAttribute.getValue() != null) {
                // put it in claims
                claimsMap.put(valueAttriubuteURI,
                        AttributeUtil.getStringValueOfAttribute(valueAttribute.getValue(), valueAttribute.getType()));

            }
        }
    }

    /*
     * set claim mapping for complex attribute
     *
     * @param entry
     * @param claimsMap
     */
    private static void setClaimsForComplexAttribute(Attribute entry, Map<String, String> claimsMap) throws
            CharonException {

        // reading attributes list of the complex attribute
        ComplexAttribute entryOfComplexAttribute = (ComplexAttribute) entry;
        Map<String, Attribute> entryAttributes = null;
        if (entryOfComplexAttribute != null) {
            if (entryOfComplexAttribute.getSubAttributesList() != null &&
                    !entryOfComplexAttribute.getSubAttributesList().isEmpty()) {
                entryAttributes = entryOfComplexAttribute.getSubAttributesList();
            }
            if (entryAttributes != null) {
                for (Attribute subEntry : entryAttributes.values()) {
                    // attribute can only be simple attribute and that also in the extension schema only
                    setClaimsForSimpleAttribute(subEntry, claimsMap);
                }
            }

        }

    }


    /*
     * Construct the SCIM Object given the attribute URIs and attribute values of the object.
     *
     * @param attributes
     * @param scimObjectType
     * @return
     */
    public static SCIMObject constructSCIMObjectFromAttributes(Map<String, String> attributes,
                                                               int scimObjectType)
            throws CharonException, NotFoundException, BadRequestException {
        SCIMObject scimObject = null;
        switch (scimObjectType) {
            case SCIMCommonConstants.GROUP:
                scimObject = new Group();
                log.debug("Building Group Object");
                break;
            case SCIMCommonConstants.USER:
                scimObject = new User();
                log.debug("Building User Object");
                break;
            default:
                break;
        }
        for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
            if (debug) {
                log.info("AttributeKey: " + attributeEntry.getKey() + " AttributeValue:" +
                        attributeEntry.getValue());
            }
            String attributeURI = attributeEntry.getKey();
            String[] attributeNames = null;

            if (attributeURI.contains(SCIMConstants.CORE_SCHEMA_URI)) {
                String[] attributeURIParts = attributeURI.split(":");
                String attributeNameString = attributeURIParts[attributeURIParts.length - 1];
                attributeNames = attributeNameString.split("\\.");
            } else {
                ArrayList<String> tempAttributeNames = new ArrayList<>();
                String extensionURI = "";
                String[] attributeURIParts = attributeURI.split(":");
                StringBuffer str = new StringBuffer();
                for (int i = 0; i < attributeURIParts.length - 1; i++) {
                    str.append(":").append(attributeURIParts[i]);
                }
                extensionURI = str.toString();
                String attributeNameString = attributeURIParts[attributeURIParts.length - 1];
                attributeNames = attributeNameString.split("\\.");
                tempAttributeNames.add(extensionURI.substring(1));

                for (int i = 0; i < attributeNames.length; i++) {
                    tempAttributeNames.add(attributeNames[i]);
                }
                attributeNames = tempAttributeNames.toArray(attributeNames);
            }

            if (attributeNames.length == 1) {

                constructSCIMObjectFromAttributesOfLevelOne(attributeEntry, scimObject, attributeNames, scimObjectType);

            } else if (attributeNames.length == 2) {

                constructSCIMObjectFromAttributesOfLevelTwo(attributeEntry, scimObject, attributeNames, scimObjectType);

            } else if (attributeNames.length == 3) {

                constructSCIMObjectFromAttributesOfLevelThree(attributeEntry, scimObject, attributeNames,
                        scimObjectType);
            }
        }
        return scimObject;
    }


    /*
     * construct the level one attributes like nickName
     *
     * @param attributeEntry
     * @param scimObject
     * @param attributeNames
     * @param scimObjectType
     * @throws BadRequestException
     * @throws CharonException
     */
    public static void constructSCIMObjectFromAttributesOfLevelOne(Map.Entry<String, String> attributeEntry,
                                                                   SCIMObject scimObject, String[] attributeNames,
                                                                   int scimObjectType)
            throws BadRequestException, CharonException {
        //get attribute schema
        AttributeSchema attributeSchema = getAttributeSchema(attributeEntry.getKey(), scimObjectType);
        if (attributeSchema != null) {
            //either simple valued or multi-valued with simple attributes
            if (attributeSchema.getMultiValued()) {
                //see whether multiple values are there
                String value = attributeEntry.getValue();
                Object[] values = value.split(",");
                //create attribute
                MultiValuedAttribute multiValuedAttribute = new MultiValuedAttribute(
                        attributeSchema.getName());
                //set values
                multiValuedAttribute.setAttributePrimitiveValues(Arrays.asList(values));
                //set attribute in scim object
                DefaultAttributeFactory.createAttribute(attributeSchema, multiValuedAttribute);
                ((AbstractSCIMObject) scimObject).setAttribute(multiValuedAttribute);

            } else {
                //convert attribute to relevant type
                Object attributeValueObject = AttributeUtil.getAttributeValueFromString(
                        attributeEntry.getValue(), attributeSchema.getType());
                //create attribute
                SimpleAttribute simpleAttribute = new SimpleAttribute(attributeNames[0],
                        attributeValueObject);
                DefaultAttributeFactory.createAttribute(attributeSchema, simpleAttribute);
                //set attribute in the SCIM object
                ((AbstractSCIMObject) scimObject).setAttribute(simpleAttribute);
            }
        }
    }

    /*
     * construct the level two attributes like emails.value
     *
     * @param attributeEntry
     * @param scimObject
     * @param attributeNames
     * @param scimObjectType
     * @throws BadRequestException
     * @throws CharonException
     * @throws NotFoundException
     */
    public static void constructSCIMObjectFromAttributesOfLevelTwo(Map.Entry<String, String> attributeEntry,
                                                                   SCIMObject scimObject, String[] attributeNames,
                                                                   int scimObjectType)
            throws BadRequestException, CharonException, NotFoundException {
        //get parent attribute name
        String parentAttributeName = attributeNames[0];
        //get parent attribute schema
        String parentAttributeURI = attributeEntry.getKey().replace("." + attributeNames[1], "");
        if (parentAttributeURI.equals(attributeEntry.getKey())) {
            parentAttributeURI = attributeEntry.getKey().replace(":" + attributeNames[1], "");
        }
        AttributeSchema parentAttributeSchema = getAttributeSchema(parentAttributeURI, scimObjectType);

                /*differentiate between sub attribute of Complex attribute and a Multivalued attribute
                with complex value*/
        if (parentAttributeSchema.getMultiValued()) {
            //get the value sub attribute
            String valueAttributeURI = attributeEntry.getKey().replace("." + attributeNames[1], "");
            AttributeSchema valueSubAttributeSchema = null;
            if (valueAttributeURI.equals(SCIMConstants.UserSchemaConstants.ADDRESSES_URI)) {
                valueAttributeURI = valueAttributeURI + ".formatted";
                valueSubAttributeSchema = getAttributeSchema(valueAttributeURI, scimObjectType);
            } else {
                valueAttributeURI = valueAttributeURI + ".value";
                valueSubAttributeSchema = getAttributeSchema(valueAttributeURI, scimObjectType);
            }
            //create map with complex value
            SimpleAttribute typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE,
                    attributeNames[1]);

            String typeAttributeURI = attributeEntry.getKey().replace("." + attributeNames[1], "");
            typeAttributeURI = typeAttributeURI + ".type";
            AttributeSchema typeAttributeSchema = getAttributeSchema(typeAttributeURI, scimObjectType);
            DefaultAttributeFactory.createAttribute(typeAttributeSchema, typeSimpleAttribute);
            SimpleAttribute valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE,
                    AttributeUtil.getAttributeValueFromString(attributeEntry.getValue(),
                            valueSubAttributeSchema.getType()));
            DefaultAttributeFactory.createAttribute(valueSubAttributeSchema, valueSimpleAttribute);

            //need to set a complex type value for multivalued attribute
            Object type = SCIMCommonConstants.DEFAULT;
            Object value = SCIMCommonConstants.DEFAULT;

            if (typeSimpleAttribute.getValue() != null) {
                type = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute.getValue() != null) {
                value = valueSimpleAttribute.getValue();
            }
            String complexName = parentAttributeName + "_" + value + "_" + type;
            ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
            DefaultAttributeFactory.createAttribute(parentAttributeSchema, complexAttribute);

            //check whether parent multivalued attribute already exists
            if (((AbstractSCIMObject) scimObject).isAttributeExist(parentAttributeName)) {
                //create attribute value as complex value
                MultiValuedAttribute multiValuedAttribute =
                        (MultiValuedAttribute) scimObject.getAttribute(parentAttributeName);
                multiValuedAttribute.setAttributeValue(complexAttribute);
            } else {
                //create the attribute and set it in the scim object
                MultiValuedAttribute multivaluedAttribute = new MultiValuedAttribute(
                        parentAttributeName);
                multivaluedAttribute.setAttributeValue(complexAttribute);
                DefaultAttributeFactory.createAttribute(parentAttributeSchema, multivaluedAttribute);
                ((AbstractSCIMObject) scimObject).setAttribute(multivaluedAttribute);
            }
        } else {
            //sub attribute of a complex attribute
            AttributeSchema subAttributeSchema = getAttributeSchema(attributeEntry.getKey(), scimObjectType);
            //we assume sub attribute is simple attribute
            SimpleAttribute simpleAttribute =
                    new SimpleAttribute(attributeNames[1],
                            AttributeUtil.getAttributeValueFromString(attributeEntry.getValue(),
                                    subAttributeSchema.getType()));
            DefaultAttributeFactory.createAttribute(subAttributeSchema, simpleAttribute);
            //check whether parent attribute exists.
            if (((AbstractSCIMObject) scimObject).isAttributeExist(parentAttributeSchema.getName())) {
                ComplexAttribute complexAttribute =
                        (ComplexAttribute) scimObject.getAttribute(parentAttributeSchema.getName());
                complexAttribute.setSubAttribute(simpleAttribute);
            } else {
                //create parent attribute and set sub attribute
                ComplexAttribute complexAttribute = new ComplexAttribute(parentAttributeSchema.getName());
                complexAttribute.setSubAttribute(simpleAttribute);
                DefaultAttributeFactory.createAttribute(parentAttributeSchema, complexAttribute);
                ((AbstractSCIMObject) scimObject).setAttribute(complexAttribute);
            }

        }
    }

    /*
     * construct the level three extension attributes like extensionSchema.manager.id
     *
     * @param attributeEntry
     * @param scimObject
     * @param attributeNames
     * @param scimObjectType
     * @throws BadRequestException
     * @throws CharonException
     */
    public static void constructSCIMObjectFromAttributesOfLevelThree(Map.Entry<String, String> attributeEntry,
                                                                     SCIMObject scimObject, String[] attributeNames,
                                                                     int scimObjectType) throws BadRequestException,
            CharonException {
        String parentAttribute = attributeNames[0];
        //get immediate parent attribute name
        String immediateParentAttributeName = attributeNames[1];

        String subAttributeURI = attributeEntry.getKey().replace("." + attributeNames[2], "");

        String parentAttributeURI = subAttributeURI.replace(":" + attributeNames[1], "");

        AttributeSchema subAttributeSchema = getAttributeSchema(subAttributeURI, scimObjectType);

        AttributeSchema attributeSchema = getAttributeSchema(parentAttributeURI, scimObjectType);

                /*differentiate between sub attribute of Complex attribute and a Multivalued attribute
                with complex value*/
        if (subAttributeSchema.getMultiValued()) {

            SimpleAttribute typeSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.TYPE,
                    attributeNames[2]);
            AttributeSchema typeAttributeSchema = getAttributeSchema(subAttributeSchema.getURI() + ".type",
                    scimObjectType);
            DefaultAttributeFactory.createAttribute(typeAttributeSchema, typeSimpleAttribute);

            AttributeSchema valueAttributeSchema = getAttributeSchema(subAttributeSchema.getURI() + ".value",
                    scimObjectType);
            SimpleAttribute valueSimpleAttribute = new SimpleAttribute(SCIMConstants.CommonSchemaConstants.VALUE,
                    AttributeUtil.getAttributeValueFromString(attributeEntry.getValue(), valueAttributeSchema.getType
                            ()));
            DefaultAttributeFactory.createAttribute(valueAttributeSchema, valueSimpleAttribute);

            //need to set a complex type value for multivalued attribute
            Object type = SCIMCommonConstants.DEFAULT;
            Object value = SCIMCommonConstants.DEFAULT;

            if (typeSimpleAttribute.getValue() != null) {
                type = typeSimpleAttribute.getValue();
            }
            if (valueSimpleAttribute.getValue() != null) {
                value = valueSimpleAttribute.getValue();
            }
            String complexName = immediateParentAttributeName + "_" + value + "_" + type;
            ComplexAttribute complexAttribute = new ComplexAttribute(complexName);
            complexAttribute.setSubAttribute(typeSimpleAttribute);
            complexAttribute.setSubAttribute(valueSimpleAttribute);
            DefaultAttributeFactory.createAttribute(subAttributeSchema, complexAttribute);

            ComplexAttribute extensionComplexAttribute = null;

            if (((AbstractSCIMObject) scimObject).isAttributeExist(parentAttribute)) {
                Attribute extensionAttribute = ((AbstractSCIMObject) scimObject).getAttribute(parentAttribute);
                extensionComplexAttribute = ((ComplexAttribute) extensionAttribute);
            } else {
                extensionComplexAttribute = new ComplexAttribute(parentAttribute);
                DefaultAttributeFactory.createAttribute(attributeSchema, extensionComplexAttribute);
                ((AbstractSCIMObject) scimObject).setAttribute(extensionComplexAttribute);
            }

            Map<String, Attribute> extensionSubAttributes = extensionComplexAttribute.getSubAttributesList();
            if (extensionSubAttributes.containsKey(attributeNames[1])) {
                //create attribute value as complex value
                MultiValuedAttribute multiValuedAttribute =
                        (MultiValuedAttribute) extensionSubAttributes.get(attributeNames[1]);
                multiValuedAttribute.setAttributeValue(complexAttribute);
            } else {
                //create the attribute and set it in the scim object
                MultiValuedAttribute multivaluedAttribute = new MultiValuedAttribute(attributeNames[1]);
                multivaluedAttribute.setAttributeValue(complexAttribute);
                DefaultAttributeFactory.createAttribute(subAttributeSchema, multivaluedAttribute);
                extensionComplexAttribute.setSubAttribute(multivaluedAttribute);
            }
        } else {

            AttributeSchema subSubAttributeSchema = getAttributeSchema(attributeEntry.getKey(), scimObjectType);
            //we assume sub attribute is simple attribute
            SimpleAttribute simpleAttribute = new SimpleAttribute(attributeNames[2],
                    AttributeUtil.getAttributeValueFromString(attributeEntry.getValue(),
                            subSubAttributeSchema.getType()));
            DefaultAttributeFactory.createAttribute(subSubAttributeSchema, simpleAttribute);

            // check if the super parent exist
            boolean superParentExist = ((AbstractSCIMObject) scimObject).isAttributeExist(attributeNames[0]);
            if (superParentExist) {
                ComplexAttribute superParentAttribute = (ComplexAttribute) ((AbstractSCIMObject) scimObject)
                        .getAttribute(attributeNames[0]);
                // check if the immediate parent exist
                boolean immediateParentExist = superParentAttribute.isSubAttributeExist(immediateParentAttributeName);
                if (immediateParentExist) {
                    // both the parent and super parent exists
                    ComplexAttribute immediateParentAttribute = (ComplexAttribute) superParentAttribute
                            .getSubAttribute(immediateParentAttributeName);
                    immediateParentAttribute.setSubAttribute(simpleAttribute);
                } else { // immediate parent does not exist
                    ComplexAttribute immediateParentAttribute = new ComplexAttribute(immediateParentAttributeName);
                    immediateParentAttribute.setSubAttribute(simpleAttribute);
                    DefaultAttributeFactory.createAttribute(subAttributeSchema, immediateParentAttribute);
                    // created the immediate parent and now set to super
                    superParentAttribute.setSubAttribute(immediateParentAttribute);
                }
            } else { // now have to create both the super parent and immediate parent
                // immediate first
                ComplexAttribute immediateParentAttribute = new ComplexAttribute(immediateParentAttributeName);
                immediateParentAttribute.setSubAttribute(simpleAttribute);
                DefaultAttributeFactory.createAttribute(subAttributeSchema, immediateParentAttribute);
                // now super parent
                AttributeSchema superParentAttributeSchema = getAttributeSchema(parentAttributeURI, scimObjectType);
                ComplexAttribute superParentAttribute = new ComplexAttribute(superParentAttributeSchema.getName());
                superParentAttribute.setSubAttribute(immediateParentAttribute);
                DefaultAttributeFactory.createAttribute(superParentAttributeSchema, superParentAttribute);
                // now add the super to the scim object
                ((AbstractSCIMObject) scimObject).setAttribute(superParentAttribute);
            }
        }
    }

    /*
     * return the attribute schema for the asked attribute URI
     *
     * @param attributeURI
     * @param scimObjectType
     * @return
     */
    private static AttributeSchema getAttributeSchema(String attributeURI, int scimObjectType) {
        ResourceTypeSchema resourceSchema = getResourceSchema(scimObjectType);
        if (resourceSchema != null) {
            List<AttributeSchema> attributeSchemas = resourceSchema.getAttributesList();
            for (AttributeSchema attributeSchema : attributeSchemas) {
                if (attributeURI.equals(attributeSchema.getURI())) {
                    return attributeSchema;
                }
                if (attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                    if (attributeSchema.getMultiValued()) {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeURI.equals(subAttributeSchema.getURI())) {
                                return subAttributeSchema;
                            }
                        }
                    } else {
                        List<SCIMAttributeSchema> subAttributeSchemaList = attributeSchema.getSubAttributeSchemas();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                            if (attributeURI.equals(subAttributeSchema.getURI())) {
                                return subAttributeSchema;
                            }
                            if (subAttributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                // this is only valid for extension schema
                                List<SCIMAttributeSchema> subSubAttributeSchemaList = subAttributeSchema
                                        .getSubAttributeSchemas();
                                for (AttributeSchema subSubAttributeSchema : subSubAttributeSchemaList) {
                                    if (attributeURI.equals(subSubAttributeSchema.getURI())) {
                                        return subSubAttributeSchema;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * return the corresponding resource type schema
     *
     * @param scimObjectType
     * @return
     */
    private static ResourceTypeSchema getResourceSchema(int scimObjectType) {
        ResourceTypeSchema resourceSchema = null;
        switch (scimObjectType) {
            case 1:
                resourceSchema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
                break;
            case 2:
                resourceSchema = SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA;
                break;
            default:
                break;
        }
        return resourceSchema;
    }

}
