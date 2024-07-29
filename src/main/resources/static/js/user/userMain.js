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

    // 폼 제출 이벤트 처리
    document.getElementById("joinModalForm").onsubmit = function(event) {
        event.preventDefault();
        // 폼 데이터 처리 로직을 여기에 추가합니다.

        alert("스터디 가입이 완료되었습니다!");
        joinModal.style.display = "none";
    }

    document.getElementById("createModalForm").onsubmit = function(event) {
        event.preventDefault();
        // 폼 데이터 처리 로직을 여기에 추가합니다.

        alert("스터디가 성공적으로 생성되었습니다!");
        createModal.style.display = "none";
    }
});