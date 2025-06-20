package com.board.Admin;

import com.board.Question.Dto.QuestionsListDto;
import com.board.User.Users;
import com.board.User.UsersRole;
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
        AdminDashboardStatsDto stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<Users> usersPage = adminService.getAllUsers(page);
        model.addAttribute("users", usersPage);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam UsersRole role,
                             @RequestParam(defaultValue = "0") int page,
                             RedirectAttributes ra) {
        adminService.changeUserRole(id, role);
        ra.addAttribute("page", page);
        return "redirect:/admin/users";
    }


    @GetMapping("/posts")
    public String posts(@RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<QuestionsListDto> posts = adminService.getQuestionsList(PageRequest.of(page, 20));
        model.addAttribute("posts", posts);
        return "admin/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Integer id,
                             @RequestParam(defaultValue = "0") int page,
                             RedirectAttributes ra) {
        try {
            adminService.deleteQuestionByAdmin(id);
            ra.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            ra.addFlashAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
        }
        ra.addAttribute("page", page);
        return "redirect:/admin/posts";
    }


    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<ReportDto> reports = adminService.getReports(PageRequest.of(page, 20));
        model.addAttribute("reports", reports);
        return "admin/reports";
    }

    @PostMapping("/reports/{id}")
    public String handleReportAction(@PathVariable Long id,
                                     @RequestParam String action,
                                     @RequestParam(defaultValue = "0") int page,
                                     RedirectAttributes ra) {
        try {
            adminService.resolveReport(id, action);
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