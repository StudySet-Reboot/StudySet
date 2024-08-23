document.addEventListener("DOMContentLoaded", function() {
    var createTaskBtn = document.getElementById("createTaskBtn");
    var createModal = document.getElementById("createModal");
    var closeButtons = document.querySelectorAll(".close-btn");

    createTaskBtn.onclick = function() {
        createModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    window.onclick = function(event) {
        if (event.target == createModal) {
            createModal.style.display = "none";
        }
    }

    // 과제 생성 폼 제출
    var createModalForm = document.getElementById("createModalForm");

    createModalForm.addEventListener("submit", function (event) {
        event.preventDefault();

        var formData = new FormData(createModalForm);

        fetch(createModalForm.action, {
            method: 'POST',
            body: formData // FormData를 직접 전송
        })
            .then(response => {
                if (response.ok) {
                    return response.text(); // 응답 본문을 텍스트로 반환
                } else {
                    throw new Error("알 수 없는 응답 상태 코드: " + response.status);
                }
            })
            .then(responseText => {
                alert("성공적으로 과제를 생성했습니다.");
                window.location.reload();
            })
            .catch(error => {
                alert("문제가 발생했습니다: " + error.message);
            });
    });

});