document.addEventListener("DOMContentLoaded", function () {
    // Mustache 템플릿
    var template = '<ul class="navbar-nav"><li class="nav-item"><a class="nav-link" href="#">로그인</a></li></ul>';

    // 데이터 (없을 경우 생략)
    var data = {};

    // Mustache 템플릿을 적용하여 HTML 생성
    var renderedHTML = Mustache.render(template, data);

    // 생성된 HTML을 원하는 위치에 추가 (여기서는 login-container에 추가)
    document.getElementById("login-container").innerHTML = renderedHTML;
});