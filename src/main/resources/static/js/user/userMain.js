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
        var xhr = new XMLHttpRequest();
        xhr.open("POST", createModalForm.action, true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                alert("스터디 그룹이 성공적으로 생성되었습니다!");
                window.location.href = "/users/main";
            } else if (xhr.readyState == 4 && xhr.status != 200) {
                alert("문제가 발생했습니다. 다시 시도해 주세요.");
            }
        };

        var urlEncodedData = "";
        var urlEncodedDataPairs = [];
        formData.forEach(function (value, key) {
            urlEncodedDataPairs.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
        });
        urlEncodedData = urlEncodedDataPairs.join("&").replace(/%20/g, "+");

        xhr.send(urlEncodedData);
    });
});