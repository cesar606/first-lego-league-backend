package cat.udl.eps.softarch.fll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import cat.udl.eps.softarch.fll.api.dto.AssignJudgeRequest;
import cat.udl.eps.softarch.fll.api.dto.AssignJudgeResponse;
import cat.udl.eps.softarch.fll.exception.RoomAssignmentException;
import cat.udl.eps.softarch.fll.service.ProjectRoomAssignmentService;

@RestController
@RequestMapping("/project-rooms")
public class ProjectRoomController {

	private final ProjectRoomAssignmentService projectRoomAssignmentService;

	public ProjectRoomController(ProjectRoomAssignmentService projectRoomAssignmentService) {
		this.projectRoomAssignmentService = projectRoomAssignmentService;
	}

	@PostMapping("/assign-judge")
	public ResponseEntity<?> assignJudge(@RequestBody AssignJudgeRequest request) {
		try {
			AssignJudgeResponse response = projectRoomAssignmentService.assignJudge(request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Captura QUALSEVOL error i retorna 400
			Map<String, String> errorBody = new HashMap<>();
			if (e instanceof RoomAssignmentException) {
				errorBody.put("error", ((RoomAssignmentException) e).getError());
				errorBody.put("message", e.getMessage());
			} else {
				errorBody.put("error", "SERVER_ERROR");
				errorBody.put("message", e.getMessage());
			}
			return ResponseEntity.status(400).body(errorBody);
		}
	}

	@ExceptionHandler(RoomAssignmentException.class)
	public ResponseEntity<Map<String, String>> handleRoomAssignmentException(RoomAssignmentException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", e.getError());
		error.put("message", e.getMessage());
		return ResponseEntity.badRequest().body(error);
	}
}
