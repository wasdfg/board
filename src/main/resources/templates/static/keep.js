document.addEventListener('DOMContentLoaded', function() {
    // 이전에 입력한 내용이 있을 경우, 해당 내용을 textarea에 설정
    var contentValue = "{{#content}}{{.}}{{/content}}";
    document.getElementById('content').value = contentValue;
});