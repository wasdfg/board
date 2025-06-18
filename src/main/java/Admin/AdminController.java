package Admin;

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

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<Users> users = adminService.getAllUsers(PageRequest.of(page, 20));
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam UsersRole role) {
        adminService.changeUserRole(id, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/questionsList")
    public String QuestionsList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<QuestionsListDto> posts = adminService.getQuestionsList(PageRequest.of(page, 20));
        model.addAttribute("questions", questions);
        return "questionsList";
    }
    @PostMapping("/questions/{id}/delete")
    public String deleteQuestionByAdmin(@PathVariable Integer id, Model model) {
        try {
            adminService.deleteQuestionByAdmin(id);
            return "redirect:/admin/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "게시글을 찾을 수 없습니다.");
            return "admin/posts";  // 혹은 에러 페이지로
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
            return "admin/posts";
        }
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<ReportDto> reports = adminService.getReports(PageRequest.of(page, 20));
        model.addAttribute("reports", reports);
        return "admin/reports";
    }
    @PostMapping("/reports/{id}/resolve")
    public String resolveReport(@PathVariable Long id,
                                @RequestParam String action) { // e.g., "DELETE_POST", "IGNORE"
        adminService.resolveReport(id, action);
        return "redirect:/admin/reports";
    }
}
