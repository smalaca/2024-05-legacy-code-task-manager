package com.smalaca.taskamanager.dto;

import com.smalaca.taskamanager.model.entities.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamDto {
    private Long id;
    private String name;
    private String codenameShort;
    private String codenameFull;
    private String description;
    private List<Long> userIds = new ArrayList<>();

    @Deprecated
    public TeamDto() {

    }

    public TeamDto(Team team) {
        this.setId(team.getId());
        this.setName(team.getName());

        if (team.getCodename() != null) {
            this.setCodenameShort(team.getCodename().getShortName());
            this.setCodenameFull(team.getCodename().getFullName());
        }

        this.setDescription(team.getDescription());
        this.setUserIds(team.getUserIds());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodenameShort() {
        return codenameShort;
    }

    public void setCodenameShort(String codenameShort) {
        this.codenameShort = codenameShort;
    }

    public String getCodenameFull() {
        return codenameFull;
    }

    public void setCodenameFull(String codenameFull) {
        this.codenameFull = codenameFull;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
