package com.outbrick.colinas.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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
            @RequestPart("profileCV") MultipartFile profileCV,
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
            profile.setProfileBirthday(new SimpleDateFormat("MM/dd/yyyy").parse(profileBirthday));
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
            ProfilesDTO addProfile = profilesService.saveProfiles(profile, profileImage, profileCV);
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
    public ResponseEntity<?> updateProfile(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileCV", required = false) MultipartFile profileCV,
            @RequestParam(value = "profileName", required = false) String profileName,
            @RequestParam(value = "profileTag", required = false) String profileTag,
            @RequestParam(value = "profileBirthday", required = false) String profileBirthday,
            @RequestParam(value = "profileEmail", required = false) String profileEmail,
            @RequestParam(value = "profilePhoneNumber", required = false) String profilePhoneNumber,
            @RequestParam(value = "profileInstagram", required = false) String profileInstagram,
            @RequestParam(value = "profileLinkedin", required = false) String profileLinkedin,
            @RequestParam(value = "profileDescription", required = false) String profileDescription,
            @RequestParam(value = "profileProfession", required = false) String profileProfession,
            @RequestParam(value = "profileOccupationArea", required = false) String profileOccupationArea,
            @RequestParam(value = "profileProfessionalExperiences", required = false) List<String> profileProfessionalExperiences,
            @RequestParam(value = "profileEducations", required = false) List<String> profileEducations,
            @RequestParam(value = "profileSkills",required = false) List<String> profileSkills,
            @RequestParam(value = "profileState", required = false) String profileState,
            @RequestParam(value = "profileCity", required = false) String profileCity,
            @RequestParam(value = "profileAddress", required = false) String profileAddress,
            @RequestParam(value = "profileGender", required = false) String profileGender
    ) throws ParseException {
        Profiles profile = new Profiles();
        profile.setProfileName(profileName);
        if(profileBirthday != null) {
            profile.setProfileBirthday(new SimpleDateFormat("MM/dd/yyyy").parse(profileBirthday));
        }
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
        ProfilesDTO getProfileAfterUpdate = profilesService.getUpdatedProfile(profileTag, profile, profileImage, profileCV);
        if(getProfileAfterUpdate != null) return ResponseEntity.ok().body(getProfileAfterUpdate);
        return ResponseEntity.badRequest().body("Não foi possivel atualizar este perfil, novo email já pertence a outro usuário");
    }

    @RequestMapping(value = "/update/password", method = RequestMethod.PUT)
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> profilePasswordChange) {
        Optional<Profiles> getProfileAfterUpdate = profilesService.updatePassword(
                profilePasswordChange.get("profileEmail"),
                profilePasswordChange.get("profilePassword")
        );
        if(getProfileAfterUpdate.isPresent()) return ResponseEntity.ok().body(getProfileAfterUpdate);
        return ResponseEntity.badRequest().body("Não foi possivel atualizar senha!");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProfile(@RequestBody Map<String, String> profileTag) {
        String tag = profileTag.get("profileTag");
        profilesService.deleteProfileByTag(tag);
        return ResponseEntity.ok().body("Perfil deletado!");
    }
}
