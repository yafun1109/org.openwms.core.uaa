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
package org.openwms.core.uaa.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A RoleVO is the representation of the role an User is assigned to.
 * 
 * @author Heiko Scherrer
 */
public class RoleVO extends SecurityObjectVO<RoleVO> implements Serializable {

    /** HTTP media type representation. */
    public static final String MEDIA_TYPE = "application/vnd.openwms.uaa.role-v1+json";

    /** The persistent key. */
    @JsonProperty("pKey")
    private String pKey;
    /** Whether or not this Role is immutable. Immutable Roles can't be modified. */
    @JsonProperty("immutable")
    private Boolean immutable;
    /** All Users assigned to the Role. */
    @JsonProperty("users")
    private Set<UserVO> users = new HashSet<>();
    /** A collection of Grants that are assigned to the Role. */
    @JsonProperty("grants")
    private Set<SecurityObjectVO> grants = new HashSet<>();

    @JsonCreator
    public RoleVO() { }

    private RoleVO(Builder builder) {
        pKey = builder.pKey;
        immutable = builder.immutable;
        users = builder.users;
        grants = builder.grants;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getpKey() {
        return pKey;
    }

    public Boolean getImmutable() {
        return immutable;
    }

    public Set<UserVO> getUsers() {
        return users;
    }

    public Set<SecurityObjectVO> getGrants() {
        return grants;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoleVO roleVO = (RoleVO) o;
        return Objects.equals(pKey, roleVO.pKey) &&
                Objects.equals(immutable, roleVO.immutable) &&
                Objects.equals(users, roleVO.users) &&
                Objects.equals(grants, roleVO.grants);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, immutable, users, grants);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return "RoleVO{" +
                "pKey='" + pKey + '\'' +
                ", immutable=" + immutable +
                ", users=" + users +
                ", grants=" + grants +
                '}';
    }

    public static final class Builder {
        private String pKey;
        private Boolean immutable;
        private Set<UserVO> users;
        private Set<SecurityObjectVO> grants;
        private @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Modify.class}) String name;
        private String description;

        private Builder() {
        }

        public static Builder aRoleVO() {
            return new Builder();
        }

        public Builder pKey(String pKey) {
            this.pKey = pKey;
            return this;
        }

        public Builder immutable(Boolean immutable) {
            this.immutable = immutable;
            return this;
        }

        public Builder users(Set<UserVO> users) {
            this.users = users;
            return this;
        }

        public Builder grants(Set<SecurityObjectVO> grants) {
            this.grants = grants;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public RoleVO build() {
            RoleVO roleVO = new RoleVO();
            roleVO.setName(name);
            roleVO.setDescription(description);
            roleVO.immutable = this.immutable;
            roleVO.users = this.users;
            roleVO.pKey = this.pKey;
            roleVO.grants = this.grants;
            return roleVO;
        }
    }
}
