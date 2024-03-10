package com.outbrick.colinas.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.outbrick.colinas.dtos.ProfilesDTO;
import com.outbrick.colinas.entities.Profiles;
import com.outbrick.colinas.services.ProfilesService;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping(value = "/profiles")
public class ProfilesController {
    
    @Autowired
    private ProfilesService profilesService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> addNewTalentProfile(
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestParam("profileName") String profileName,
            @RequestParam("profileBirthday") String profileBirthday,
            @RequestParam("profilePassword") String profilePassword,
            @RequestParam("profileEmail") String profileEmail,
            @RequestParam("profilePhoneNumber") String profilePhoneNumber,
            @RequestParam("profileInstagram") String profileInstagram,
            @RequestParam("profileLinkedin") String profileLinkedin,
            @RequestParam("profileDescription") String profileDescription,
            @RequestParam("profileProfession") String profileProfession,
            @RequestParam("profileOccupationArea") String profileOccupationArea,
            @RequestParam("profileProfessionalExperiences") List<String> profileProfessionalExperiences,
            @RequestParam("profileEducations") List<String> profileEducations,
            @RequestParam("profileSkills") List<String> profileSkills,
            @RequestParam("profileState") String profileState,
            @RequestParam("profileCity") String profileCity,
            @RequestParam("profileAddress") String profileAddress,
            @RequestParam("profileGender") String profileGender
    ) {
        try {
            Profiles profile = new Profiles();
            profile.setProfileName(profileName);
            profile.setProfileBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(profileBirthday));
            profile.setProfileCreateAt(Calendar.getInstance().getTime());
            profile.setProfileStatus("Ativo");
            profile.setProfilePassword(profilePassword);
            profile.setProfileEmail(profileEmail);
            profile.setProfilePhoneNumber(profilePhoneNumber);
            profile.setProfileInstagram(profileInstagram);
            profile.setProfileLinkedin(profileLinkedin);
            profile.setProfileDescription(profileDescription);
            profile.setProfileProfession(profileProfession);
            profile.setProfileOccupationArea(profileOccupationArea);
            profile.setProfileProfessionalExperiences(profileProfessionalExperiences);
            profile.setProfileEducations(profileEducations);
            profile.setProfileSkills(profileSkills);
            profile.setProfileState(profileState);
            profile.setProfileCity(profileCity);
            profile.setProfileAddress(profileAddress);
            profile.setProfileGender(profileGender);

            ProfilesDTO addProfile = profilesService.saveProfiles(profile, profileImage);
            if (addProfile != null) return ResponseEntity.ok().body(addProfile);
            return ResponseEntity.badRequest().body("Um perfil com esse email, já existe no nosso banco de talentos!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Não foi possível adicionar seu perfil. Tente novamente mais tarde.");
        }
    }
    
    @RequestMapping(value = "/find/tag/{profileTag}", method = RequestMethod.GET)
    public ResponseEntity<?> findProfilesByTag(@PathVariable(value = "profileTag") String profileTag) throws IOException {
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
