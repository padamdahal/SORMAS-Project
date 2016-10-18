package de.symeda.sormas.backend.task;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "TaskFacade")
public class TaskFacadeEjb implements TaskFacade {
	
	@EJB
	private TaskService service;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;

	public Task fromDto(TaskDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Task task = service.getByUuid(dto.getUuid());
		if (task == null) {
			task = new Task();
			task.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				task.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		} 
		
		Task a = task;
		TaskDto b = dto;
		
		a.setAssigneeUser(DtoHelper.fromReferenceDto(b.getAssigneeUser(), userService));
		a.setAssigneeReply(b.getAssigneeReply());
		a.setCreatorUser(DtoHelper.fromReferenceDto(b.getCreatorUser(), userService));
		a.setCreatorComment(b.getCreatorComment());
		a.setDueDate(b.getDueDate());
		a.setPerceivedStart(b.getPerceivedStart());
		a.setStatusChangeDate(b.getStatusChangeDate());
		a.setTaskStatus(b.getTaskStatus());
		a.setTaskType(b.getTaskType());
		
		a.setTaskContext(b.getTaskContext());
		if (b.getTaskContext() != null) {
			switch (b.getTaskContext()) {
			case CASE:
				a.setCaze(DtoHelper.fromReferenceDto(b.getCaze(), caseService));
	//			a.setEvent(null);
	//			a.setContact(null);
				break;
				default:
					throw new UnsupportedOperationException(b.getTaskContext() + " is not implemented");
			}
		} else {
			a.setCaze(null);
//			a.setEvent(null);
//			a.setContact(null);
		}
		
		return task;
	}
	
	public static TaskDto toDto(Task task) {
		
		if (task == null) {
			return null;
		}

		TaskDto a = new TaskDto();
		Task b = task;
		
		a.setCreationDate(b.getCreationDate());
		a.setChangeDate(b.getChangeDate());
		a.setUuid(b.getUuid());
		
		a.setAssigneeUser(DtoHelper.toReferenceDto(b.getAssigneeUser()));
		a.setAssigneeReply(b.getAssigneeReply());
		a.setCreatorUser(DtoHelper.toReferenceDto(b.getCreatorUser()));
		a.setCreatorComment(b.getCreatorComment());
		a.setDueDate(b.getDueDate());
		a.setPerceivedStart(b.getPerceivedStart());
		a.setStatusChangeDate(b.getStatusChangeDate());
		a.setTaskContext(b.getTaskContext());
		a.setTaskStatus(b.getTaskStatus());
		a.setTaskType(b.getTaskType());	
		a.setCaze(DtoHelper.toReferenceDto(b.getCaze()));

		return a;
	}

	@Override
	public TaskDto saveTask(TaskDto dto) {
		Task ado = fromDto(dto);
		service.ensurePersisted(ado);
		return toDto(ado);	
	}
	
	@Override
	public List<TaskDto> getAllAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
}

