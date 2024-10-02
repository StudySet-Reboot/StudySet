import {displayErrorToast, displayToast} from "../common/toast.js";

document.addEventListener("DOMContentLoaded", function() {
    // 스터디 가입 폼 제출
    const joinModalForm = document.getElementById("joinModalForm");
    joinModalForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(joinModalForm);
        const urlEncodedData = new URLSearchParams(formData).toString();

        fetch(joinModalForm.action, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: urlEncodedData
        })
            .then(response => {
                if (response.ok) {
                    if (response.status === 200) {
                        // 성공 응답
                        displayToast("성공적으로 그룹에 가입했습니다!");
                        setTimeout(() => window.location.href = "/users/main", 1000);
                    } else {
                        displayErrorToast("알 수 없는 응답 상태 코드: " + response.status);
                    }
                } else {
                    // 에러 응답 (본문 있음)
                    return response.json().then(err => {
                        throw new Error(err.message);
                    });
                }
            })
            .catch(error => {
                displayErrorToast(`${error.message}`);
            });
    });

    // 스터디 생성 폼 제출
    const createModalForm = document.getElementById("createModalForm");
    createModalForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(createModalForm);
        const urlEncodedData = new URLSearchParams(formData).toString();

        fetch(createModalForm.action, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: urlEncodedData
        })
            .then(response => {
                if (response.ok) {
                    if (response.status === 200) {
                        displayToast("그룹이 생성되었습니다!");
                        setTimeout(() => window.location.href = "/users/main", 1000);
                    } else {
                        displayErrorToast("알 수 없는 응답 상태 코드: " + response.status);
                    }
                } else {
                    return response.json().then(err => {
                        throw new Error(err.message);
                    });
                }
            })
            .catch(error => {
                displayErrorToast(`${error.message}`);
            });
    });

    /* 모달창 code 작성 시 숫자 6자리 제한
    document.addEventListener('DOMContentLoaded', () => {
        const inputElements = document.querySelectorAll('input[name="code"]');

        inputElements.forEach(input => {
            input.addEventListener('input', (event) => {
                const input = event.target;
                input.value = input.value.replace(/[^0-9]/g, ''); // 숫자만 허용
                if (input.value.length > 6) {
                    input.value = input.value.slice(0, 6); // 6자리로 제한
                }
            });
        });
    }); */
});