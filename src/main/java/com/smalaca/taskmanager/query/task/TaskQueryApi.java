package com.smalaca.taskmanager.query.task;

import com.smalaca.taskamanager.dto.AssigneeDto;
import com.smalaca.taskamanager.dto.StakeholderDto;
import com.smalaca.taskamanager.dto.TaskDto;
import com.smalaca.taskamanager.dto.WatcherDto;
import com.smalaca.taskamanager.model.embedded.EmailAddress;
import com.smalaca.taskamanager.model.embedded.Owner;
import com.smalaca.taskamanager.model.embedded.PhoneNumber;
import com.smalaca.taskamanager.model.entities.Story;
import com.smalaca.taskamanager.model.entities.Task;
import com.smalaca.taskamanager.repository.TaskRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskQueryApi {
    private TaskRepository taskRepository;

    public TaskQueryApi(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ResponseEntity<TaskDto> findById(Long id) {
        Optional<Task> found = taskRepository.findById(id);

        if (found.isPresent()) {
            Task task = found.get();
            TaskDto dto = new TaskDto();

            dto.setId(task.getId());
            dto.setDescription(task.getDescription());
            dto.setTitle(task.getTitle());
            dto.setStatus(task.getStatus().name());

            if (task.getStory() != null) {
                Story project = task.getStory();
                dto.setStoryId(project.getId());
            }

            Owner owner = task.getOwner();

            if (owner != null) {
                dto.setOwnerLastName(owner.getLastName());
                dto.setOwnerFirstName(owner.getFirstName());

                PhoneNumber phoneNumber = owner.getPhoneNumber();

                if (phoneNumber != null) {
                    dto.setOwnerPhoneNumberNumber(phoneNumber.getNumber());
                    dto.setOwnerPhoneNumberPrefix(phoneNumber.getPrefix());
                }

                EmailAddress emailAddress = owner.getEmailAddress();

                if (emailAddress != null) {
                    dto.setOwnerEmailAddress(emailAddress.getEmailAddress());
                }
            }

            List<WatcherDto> watchers = task.getWatchers().stream().map(watcher -> {
                WatcherDto wDto = new WatcherDto();
                wDto.setLastName(watcher.getLastName());
                wDto.setFirstName(watcher.getFirstName());
                if (watcher.getEmailAddress() != null) {
                    wDto.setEmailAddress(watcher.getEmailAddress().getEmailAddress());
                }
                if (watcher.getPhoneNumber() != null) {
                    wDto.setPhoneNumber(watcher.getPhoneNumber().getNumber());
                    wDto.setPhonePrefix(watcher.getPhoneNumber().getPrefix());
                }
                return wDto;
            }).collect(Collectors.toList());
            dto.setWatchers(watchers);
            
            if (task.getAssignee() != null) {
                AssigneeDto aDto = new AssigneeDto();
                aDto.setTeamId(task.getAssignee().getTeamId());
                aDto.setLastName(task.getAssignee().getLastName());
                aDto.setFirstName(task.getAssignee().getFirstName());
                dto.setAssignee(aDto);
            }

            List<StakeholderDto> stakeholders = task.getStakeholders().stream().map(stakeholder -> {
                StakeholderDto sDto = new StakeholderDto();
                sDto.setLastName(stakeholder.getLastName());
                sDto.setFirstName(stakeholder.getFirstName());
                if (stakeholder.getEmailAddress() != null) {
                    sDto.setEmailAddress(stakeholder.getEmailAddress().getEmailAddress());
                }
                if (stakeholder.getPhoneNumber() != null) {
                    sDto.setPhoneNumber(stakeholder.getPhoneNumber().getNumber());
                    sDto.setPhonePrefix(stakeholder.getPhoneNumber().getPrefix());
                }
                return sDto;
            }).collect(Collectors.toList());
            dto.setStakeholders(stakeholders);

            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
}
