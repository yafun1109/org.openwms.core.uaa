/*
 * Copyright 2005-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.core.uaa.admin;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openwms.core.uaa.admin.impl.User;
import org.openwms.core.uaa.admin.impl.UserDetails;
import org.openwms.core.uaa.api.UserDetailsVO;
import org.openwms.core.uaa.api.UserVO;

import javax.validation.Valid;
import java.util.Base64;
import java.util.List;

/**
 * A UserMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(implementationPackage = "org.openwms.core.uaa.admin.impl")
public interface UserMapper {

    @Mapping(source = "persistentKey", target = "pKey")
    UserVO convertToVO(User eo);

    List<UserVO> convertToVO(List<User> eo);

    @Mapping(source = "pKey", target = "persistentKey")
    User convertFrom(UserVO vo);

    @AfterMapping
    default void enhanceEmail(@MappingTarget User parent){
        var childList = parent.getEmailAddresses();
        if (childList != null) {
            childList.forEach(child -> child.setUser(parent));
        }
    }

    @Valid UserDetailsVO map(UserDetails eo);

    default byte[] map(String source) {
        if (source == null) {
            return null;
        }
        return Base64.getDecoder().decode(source);
    }
}
