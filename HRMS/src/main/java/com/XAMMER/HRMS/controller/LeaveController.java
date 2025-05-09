// package com.XAMMER.HRMS.controller;


// import com.XAMMER.HRMS.model.Leave;
// import com.XAMMER.HRMS.service.LeaveService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/leave")
// public class LeaveController {

//     @Autowired
//     private LeaveService leaveService;

//     @GetMapping
//     public List<Leave> getAllLeaves() {
//         return leaveService.getAllLeaves();
//     }

//     @GetMapping("/{id}")
//     public Optional<Leave> getLeaveById(@PathVariable Long id) {
//         return leaveService.getLeaveById(id);
//     }

//     @PostMapping
//     public Leave applyLeave(@RequestBody Leave leave) {
//         return leaveService.applyLeave(leave);
//     }

//     @DeleteMapping("/{id}")
//     public void deleteLeave(@PathVariable Long id) {
//         leaveService.deleteLeave(id);
//     }
// }
