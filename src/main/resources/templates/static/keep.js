// 제목과 내용에 대한 저장된 값이 있는지 확인
const storedTitle = localStorage.getItem('storedTitle');
const storedContent = localStorage.getItem('storedContent');

// 저장된 값들을 각각의 입력 필드에 적용
if (storedTitle) {
    document.getElementById('title').value = storedTitle;
}

if (storedContent) {
    document.getElementById('content').value = storedContent;
}

// 입력이 변경될 때마다 값을 로컬 스토리지에 저장
document.getElementById('title').addEventListener('input', function () {
    localStorage.setItem('storedTitle', this.value);
});

document.getElementById('content').addEventListener('input', function () {
    localStorage.setItem('storedContent', this.value);
});