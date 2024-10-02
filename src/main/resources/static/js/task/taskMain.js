import { displayErrorToast, displayToast } from "../common/toast.js";

// 과제 생성 폼 제출
const createModalForm = document.getElementById("createModalForm");

createModalForm.addEventListener("submit", function (event) {
    event.preventDefault(); // 폼 제출 막음

    const startTime = new Date(document.getElementById("startTime").value);
    const endTime = new Date(document.getElementById("endTime").value);

    // 날짜 유효성 검사
    if (startTime > endTime) {
        displayErrorToast("마감 기한은 시작 기한보다 앞서야 합니다.");
        return;
    }

    const formData = new FormData(createModalForm);

    fetch(createModalForm.action, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error("알 수 없는 응답 상태 코드: " + response.status);
            }
        })
        .then(data => {
            displayToast("성공적으로 과제를 생성했습니다.");
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        })
        .catch(error => {
            displayErrorToast("문제가 발생했습니다: " + error.message);
        });
});

// 현재가 과제 시작기한보다 앞서면 링크 비활성화
const taskLinks = document.querySelectorAll('.task-link');

taskLinks.forEach(function (link) {
    const startTime = new Date(link.getAttribute('data-start-time'));
    const currentTime = new Date();

    if (startTime > currentTime) {
        link.style.pointerEvents = 'none';
        link.style.color = '#ccc';
    }
});

// 과제 진행률 계산 후 전송
const timelines = document.querySelectorAll('.task-timeline');

timelines.forEach(function(timeline) {
    const startTime = new Date(timeline.getAttribute('data-start-time'));
    const endTime = new Date(timeline.getAttribute('data-end-time'));
    const currentTime = new Date();

    const totalDuration = endTime - startTime;
    const elapsedDuration = currentTime - startTime;

    let progressPercent = (elapsedDuration / totalDuration) * 100;
    progressPercent = Math.max(0, Math.min(100, progressPercent));
    const progressBar = timeline.querySelector('.progress-bar');
    progressBar.style.width = progressPercent + '%';
});
