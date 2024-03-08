package com.outbrick.colinas.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.outbrick.colinas.dtos.ProfilesDTO;
import com.outbrick.colinas.entities.Profiles;
import com.outbrick.colinas.services.ProfilesService;

@Controller
@RequestMapping(value = "/profiles")
public class ProfilesController {
    
    @Autowired
    private ProfilesService profilesService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> addNewTalentProfile(@RequestBody Profiles profiles) {
        try {
            ProfilesDTO addProfile = profilesService.saveProfiles(profiles);
            if (addProfile != null) return ResponseEntity.ok().body(addProfile);
            else return ResponseEntity.badRequest().body("Um perfil com esse email, já existe no nosso banco de talentos!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Não foi possível adicionar seu perfil. Tente novamente mais tarde.");
        }
    }
    
    @RequestMapping(value = "/find/tag/{profileTag}", method = RequestMethod.GET)
    public ResponseEntity<?> findProfilesByTag(@PathVariable(value = "profileTag") String profileTag) {
        ProfilesDTO getProfile = profilesService.searchProfileTag(profileTag);
        if(getProfile != null) return ResponseEntity.ok().body(getProfile);
        return ResponseEntity.badRequest().body("Não foi possivel encontrar o perfil com a tag fornecida!");
    }

    @RequestMapping(value = "/find/all", method = RequestMethod.GET)
    public ResponseEntity<?> findAllProfiles() {
        List<ProfilesDTO> allProfiles = profilesService.getAllProfiles();
        if(!allProfiles.isEmpty()) return ResponseEntity.ok().body(allProfiles);
        return ResponseEntity.badRequest().body("Ainda não existe perfis cadastrados no sistema!"); 
    }

    @RequestMapping(value = "/find/login", method = RequestMethod.POST)
    public ResponseEntity<?> findProfileByLogin(@RequestBody Map<String, String> profileLogin) {
        ProfilesDTO getProfile = profilesService.findProfileByLogin(
            profileLogin.get("profileEmail"), 
            profileLogin.get("profilePassword")
        );

        if(getProfile != null) return ResponseEntity.ok().body(getProfile);
        return ResponseEntity.badRequest().body("Email ou a senha estão incorretas, verifique esses campos e tente novamente!");
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProfile(@RequestBody Profiles updatedProfile) {
        ProfilesDTO getProfileAfterUpdate = profilesService.getUpdatedProfile(updatedProfile.getProfileTag(), updatedProfile);
        if(getProfileAfterUpdate != null) return ResponseEntity.ok().body(getProfileAfterUpdate);
        return ResponseEntity.badRequest().body("Não foi possivel atualizar este perfil, novo email já pertence a outro usuário");
    }    
}
