/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.core.uaa.admin.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * An UserRepository offers functionality regarding {@link User} entity classes.
 *
 * @author Heiko Scherrer
 */
interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBypKey(String pKey);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPersistedPassword(String username, String persistedPassword);

    @Modifying
    @Query("delete from User u where u.pKey = :pKey")
    void deleteByPkey(@Param("pKey") String pKey);
}