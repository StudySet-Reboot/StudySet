document.addEventListener("DOMContentLoaded", function() {
    var joinStudyBtn = document.getElementById("joinStudyBtn");
    var createStudyBtn = document.getElementById("createStudyBtn");
    var joinModal = document.getElementById("joinModal");
    var createModal = document.getElementById("createModal");
    var closeButtons = document.querySelectorAll(".close-btn");

    joinStudyBtn.onclick = function() {
        joinModal.style.display = "block";
    }

    createStudyBtn.onclick = function() {
        createModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    window.onclick = function(event) {
        if (event.target == joinModal) {
            joinModal.style.display = "none";
        }
        if (event.target == createModal) {
            createModal.style.display = "none";
        }
    }

    // 스터디 가입 폼 제출
    var joinModalForm = document.getElementById("joinModalForm");
    joinModalForm.addEventListener("submit", function (event) {
        event.preventDefault();

        var formData = new FormData(joinModalForm);
        var urlEncodedData = new URLSearchParams(formData).toString();

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
                        // 성공 응답 (본문 없음)
                        alert("성공적으로 그룹에 가입했습니다!");
                        window.location.href = "/users/main";
                    } else {
                        // 다른 상태 코드 처리 (필요한 경우)
                        alert("알 수 없는 응답 상태 코드: " + response.status);
                    }
                } else {
                    // 에러 응답 (본문 있음)
                    return response.json().then(err => {
                        throw new Error(err.message || "문제가 발생했습니다.");
                    });
                }
            })
            .catch(error => {
                alert(`${error.message}`);
            });
    });

    // 스터디 생성 폼 제출
    var createModalForm = document.getElementById("createModalForm");
    createModalForm.addEventListener("submit", function (event) {
        event.preventDefault();

        var formData = new FormData(createModalForm);
        var urlEncodedData = new URLSearchParams(formData).toString();

        fetch(createModalForm.action, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: urlEncodedData
        })
            .then(response => {
                if (response.ok) {
                    alert("스터디 그룹이 성공적으로 생성되었습니다!");
                    window.location.href = "/users/main";
                } else {
                    alert('문제가 발생했습니다. 다시 시도해 주세요.');
                }
            })
            .catch(error => {
                alert(error.message);
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