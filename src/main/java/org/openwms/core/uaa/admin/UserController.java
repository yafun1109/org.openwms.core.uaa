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
package org.openwms.core.uaa.admin;

import org.ameba.http.MeasuredRestController;
import org.openwms.core.exception.InvalidPasswordException;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.openwms.core.uaa.api.PasswordString;
import org.openwms.core.uaa.api.RoleVO;
import org.openwms.core.uaa.api.SecurityObjectVO;
import org.openwms.core.uaa.api.UserVO;
import org.openwms.core.uaa.api.ValidationGroups;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.openwms.core.uaa.api.UAAConstants.API_USERS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * An UserController represents a RESTful access to {@code User}s. It is transactional by the means it is the outer application service
 * facade that returns validated and completed {@code User} objects to its clients.
 *
 * @author Heiko Scherrer
 */
@MeasuredRestController
public class UserController extends AbstractWebController {

    private final UserService service;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final SecurityObjectMapper securityObjectMapper;

    public UserController(UserService service, UserMapper userMapper, RoleMapper roleMapper, SecurityObjectMapper securityObjectMapper) {
        this.service = service;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.securityObjectMapper = securityObjectMapper;
    }

    @GetMapping(API_USERS + "/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(UserController.class).findAllUsers()).withRel("users-findall"),
                        linkTo(methodOn(UserController.class).findUser("pKey")).withRel("users-findbypkey"),
                        linkTo(methodOn(UserController.class).findGrantsForUser("pKey")).withRel("users-findgrants"),
                        linkTo(methodOn(UserController.class).create(new UserVO(), null)).withRel("users-create"),
                        linkTo(methodOn(UserController.class).save(new UserVO())).withRel("users-save"),
                        linkTo(methodOn(UserController.class).saveImage("", "pKey")).withRel("users-saveimage"),
                        linkTo(methodOn(UserController.class).updatePassword("pKey", new PasswordString("newPassword"))).withRel("users-change-password"),
                        linkTo(methodOn(UserController.class).delete("pKey")).withRel("users-delete")
                )
        );
    }

    @GetMapping(API_USERS)
    public ResponseEntity<List<UserVO>> findAllUsers() {
        return ResponseEntity.ok(userMapper.convertToVO(new ArrayList<>(service.findAll())));
    }

    @GetMapping(API_USERS + "/{pKey}")
    public ResponseEntity<UserVO> findUser(@PathVariable("pKey") @NotEmpty String pKey) {
        return ResponseEntity.ok(userMapper.convertToVO(service.findByPKey(pKey)));
    }

    @Transactional(readOnly = true)
    @GetMapping(API_USERS + "/{pKey}/grants")
    public ResponseEntity<List<SecurityObjectVO>> findGrantsForUser(@PathVariable("pKey") @NotEmpty String pKey) {
        var user = service.findByPKey(pKey);
        return ResponseEntity.ok(securityObjectMapper.convertToVO(user.getGrants()));
    }

    @Transactional(readOnly = true)
    @GetMapping(API_USERS + "/{pKey}/roles")
    public ResponseEntity<List<RoleVO>> findRolesForUser(@PathVariable("pKey") @NotEmpty String pKey) {
        var user = service.findByPKey(pKey);
        return ResponseEntity.ok(roleMapper.convertToVO(user.getRoles()));
    }

    @PostMapping(API_USERS)
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<UserVO> create(
            @RequestBody @Valid @NotNull UserVO vo,
            HttpServletRequest req) {
        var user = userMapper.convertFrom(vo);
        var created = service.create(user, vo.getRoleNames());
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, created.getPersistentKey()))
                .body(userMapper.convertToVO(created));
    }

    @PutMapping(API_USERS)
    public ResponseEntity<UserVO> save(@RequestBody @Valid UserVO vo) {
        var user = userMapper.convertFrom(vo);
        return ResponseEntity.ok(userMapper.convertToVO(service.save(user, vo.getRoleNames())));
    }

    @PostMapping(API_USERS + "/{pKey}/details/image")
    public ResponseEntity<Void> saveImage(@RequestBody @NotNull String image, @PathVariable("pKey") @NotEmpty String pKey) {
        service.uploadImageFile(pKey, image.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok().build();
    }

    @PostMapping(API_USERS + "/{pKey}/password")
    public ResponseEntity<UserVO> updatePassword(
            @PathVariable("pKey") @NotEmpty String pKey,
            @RequestBody @NotNull PasswordString password) {
        try {
            return ResponseEntity.ok(service.updatePassword(pKey, password.asValue()));
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(API_USERS + "/{pKey}")
    public ResponseEntity<Void> delete(@PathVariable("pKey") @NotEmpty String pKey) {
        service.delete(pKey);
        return ResponseEntity.noContent().build();
    }
}
