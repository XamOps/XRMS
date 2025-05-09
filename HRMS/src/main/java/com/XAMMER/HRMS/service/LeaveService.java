// package com.XAMMER.HRMS.service;
// //package com.xammer.hrms.service;

// import com.XAMMER.HRMS.model.Leave;
// import com.XAMMER.HRMS.repository.LeaveRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;

// @Service
// public class LeaveService {

//     @Autowired
//     private LeaveRepository leaveRepository;

//     public List<Leave> getAllLeaves() {
//         return leaveRepository.findAll();
//     }

//     public Optional<Leave> getLeaveById(Long id) {
//         return leaveRepository.findById(id);
//     }

//     public Leave applyLeave(Leave leave) {
//         return leaveRepository.save(leave);
//     }

//     public void deleteLeave(Long id) {
//         leaveRepository.deleteById(id);
//     }
// }
