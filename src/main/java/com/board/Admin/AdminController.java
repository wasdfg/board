package com.board.Admin;

import com.board.Admin.report.Report;
import com.board.Question.Dto.QuestionsListDto;
import com.board.User.Users;
import com.board.User.UsersRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        //AdminDashboardStatsDto stats = adminService.getDashboardStats();
        //model.addAttribute("stats", stats);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<Users> usersPage = adminService.getAllUsers(page);
        model.addAttribute("users", usersPage);
        return "admin/users";
    }

    @PostMapping("/users/{id}")
    public String changeUsersRole(@PathVariable Long id,
                                   @RequestParam String action,
                                   @RequestParam(required = false) UsersRole role,
                                   @RequestParam(defaultValue = "0") int page,
                                   RedirectAttributes ra) {
        if ("changeRole".equals(action) && role != null) {
            adminService.changeUsersRole(id, role);
            ra.addFlashAttribute("successMessage", "권한이 변경되었습니다.");
        } else {
            ra.addFlashAttribute("errorMessage", "올바르지 않은 요청입니다.");
        }
        ra.addAttribute("page", page);
        return "redirect:/admin/users";
    }


    @GetMapping("/posts")
    public String posts(@RequestParam(defaultValue = "0") int page, Model model) {
        //Page<QuestionsListDto> posts = adminService.getQuestionsList(page);
        //model.addAttribute("posts", posts);
        return "admin/posts";
    }

    @PostMapping("/posts/{id}")
    public String deletePosts(@PathVariable Integer id,
                                   @RequestParam String action,
                                   @RequestParam(defaultValue = "0") int page,
                                   RedirectAttributes ra) {
        if ("delete".equals(action)) {
            try {
                //adminService.deleteQuestionByAdmin(id);
                ra.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
            } catch (EntityNotFoundException e) {
                ra.addFlashAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
            } catch (Exception e) {
                ra.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
            }
        }
        ra.addAttribute("page", page);
        return "redirect:/admin/posts";
    }


    @GetMapping("/reports")
    public String checkReports(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Report> reports = adminService.getReports(page);
        model.addAttribute("reports", reports);
        return "admin/reports";
    }

    @PostMapping("/reports/{id}")
    public String reportAction(@PathVariable Long id,
                                     @RequestParam String action,
                                     @RequestParam(defaultValue = "0") int page,
                                     RedirectAttributes ra) {
        try {
            //adminService.resolveReport(id, action);
            ra.addFlashAttribute("successMessage", "신고가 처리되었습니다.");
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("errorMessage", "신고를 찾을 수 없습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "신고 처리 중 오류가 발생했습니다.");
        }
        ra.addAttribute("page", page);
        return "redirect:/admin/reports";
    }
}