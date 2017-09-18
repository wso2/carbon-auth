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

package org.wso2.carbon.auth.scim.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.List;
import java.util.Map;

/**
 * This is the wrapper class of Charon User Manager. This deals with the user management API. 
 * 
 */
public class CarbonAuthUserManager implements UserManager {
    private static Logger log = LoggerFactory.getLogger(CarbonAuthUserManager.class);
    UserStoreConnector userStoreConnector;
    
    public CarbonAuthUserManager(UserStoreConnector userStoreConnector) {
        this.userStoreConnector = userStoreConnector;
    }    

    @Override
    public User createUser(User user, Map<String, Boolean> attributes) throws CharonException, ConflictException,
            BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public User getUser(String arg0, Map<String, Boolean> arg1) throws CharonException, BadRequestException,
            NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> listUsersWithGET(Node arg0, int arg1, int arg2, String arg3, String arg4,
            Map<String, Boolean> arg5) throws CharonException, NotImplementedException, BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> listUsersWithPost(SearchRequest arg0, Map<String, Boolean> arg1) throws CharonException,
            NotImplementedException, BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public User updateUser(User arg0, Map<String, Boolean> arg1) throws NotImplementedException, CharonException,
            BadRequestException, NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void deleteUser(String arg0) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Group createGroup(Group arg0, Map<String, Boolean> arg1) throws CharonException, ConflictException,
            NotImplementedException, BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Group getGroup(String arg0, Map<String, Boolean> arg1) throws NotImplementedException, BadRequestException,
            CharonException, NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Object> listGroupsWithGET(Node arg0, int arg1, int arg2, String arg3, String arg4,
            Map<String, Boolean> arg5) throws CharonException, NotImplementedException, BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> listGroupsWithPost(SearchRequest arg0, Map<String, Boolean> arg1)
            throws NotImplementedException, BadRequestException, CharonException {
        // TODO Auto-generated method stub
        return null;
    }    

    @Override
    public Group updateGroup(Group arg0, Group arg1, Map<String, Boolean> arg2) throws NotImplementedException,
            BadRequestException, CharonException, NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void deleteGroup(String arg0) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public User createMe(User arg0, Map<String, Boolean> arg1) throws CharonException, ConflictException,
            BadRequestException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public User getMe(String arg0, Map<String, Boolean> arg1) throws CharonException, BadRequestException,
            NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public User updateMe(User arg0, Map<String, Boolean> arg1) throws NotImplementedException, CharonException,
            BadRequestException, NotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteMe(String arg0) throws NotFoundException, CharonException, NotImplementedException,
            BadRequestException {
        // TODO Auto-generated method stub
        
    }

}
