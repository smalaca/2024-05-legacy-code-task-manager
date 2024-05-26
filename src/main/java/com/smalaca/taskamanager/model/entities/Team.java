package com.smalaca.taskamanager.model.entities;

import com.smalaca.taskamanager.dto.TeamDto;
import com.smalaca.taskamanager.model.embedded.Codename;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private Codename codename;

    private String description;

    @OneToMany
    private List<User> members = new ArrayList<>();

    @ManyToOne
    private Project project;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<User> members) {
        this.members = new ArrayList<>(members);
    }

    public List<User> getMembers() {
        return members;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        if (!members.contains(user)) {
            throw new RuntimeException();
        }
        members.remove(user);
    }

    public Codename getCodename() {
        return codename;
    }

    public void setCodename(Codename codename) {
        this.codename = codename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Team team = (Team) o;

        return new EqualsBuilder()
                .append(id, team.id)
                .append(name, team.name)
                .append(codename, team.codename)
                .append(description, team.description)
                .isEquals();
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(codename)
                .append(description)
                .toHashCode();
    }

    public TeamDto asTeamDto() {
        TeamDto dto = new TeamDto();
        dto.setId(id);
        dto.setName(name);

        if (hasCodename()) {
            dto.setCodenameShort(codename.getShortName());
            dto.setCodenameFull(codename.getFullName());
        }

        dto.setDescription(description);
        dto.setUserIds(getUserIds());

//        TeamDto dto = new TeamDto(this);
//
//        TeamDto dto = new TeamDto(...);
//
//        TeamDto dto = TeamDto.create(...);
//
//        TeamDto dto = new TeamDtoFactory().create(
//                id, name, codename.getShortName(), codename.getFullName(), description, getUserIds());
//
//        TeamDto dto = new TeamDtoBuilder()
//                .withId(id)
//                .withName(name)
//                .with()
//                .build();

        return dto;
    }

    private boolean hasCodename() {
        return codename != null;
    }

    private List<Long> getUserIds() {
        return members.stream().map(User::getId).collect(toList());
    }
}
