document.addEventListener("DOMContentLoaded", function() {
    // 그룹 코드 확인
    var toast = document.getElementById("toast");
    var btn = document.getElementById("groupCodeBtn");
    var groupCodeDisplay = document.getElementById("groupCodeDisplay");
    var groupCodeDisplayText = document.getElementById("groupCodeDisplayText");

    btn.onclick = function(event) {
        event.preventDefault();

        // 그룹 코드 값을 읽어오기
        var groupCode = groupCodeDisplay.getAttribute("data-group-code");

        groupCodeDisplayText.innerText = groupCode;
        toast.className = "toast show";

        // 3초 후 토스트 숨김
        setTimeout(function() {
            toast.className = toast.className.replace("show", "");
        }, 3000);
    };

    // 그룹원 검색
    var searchBtn = document.getElementById("searchMemberBtn");
    var searchModal = document.getElementById("searchModal");
    var closeButtons = document.querySelectorAll(".close-btn");

    searchBtn.onclick = function() {
        searchModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    window.onclick = function(event) {
        if (event.target == searchModal) {
            joinModal.style.display = "none";
        }
    }

    // 그룹원 검색 키워드 제출
    var searchModalForm = document.getElementById("searchModalForm");
    searchModalForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼의 기본 제출 동작 방지

        // 폼 데이터 가져오기
        var form = event.target;
        var keyword = form.querySelector("#keyword").value;
        var groupId = form.querySelector("#groupId").value;

        // URL 쿼리 문자열 만들기
        var url = new URL(form.action, window.location.origin);
        url.searchParams.append('keyword', keyword);
        url.searchParams.append('groupId', groupId);

        fetch(url, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message);
                    });
                }
                return response.text();  // 정상적인 경우 HTML을 텍스트로 반환
            })
            .then(data => {
                document.querySelector('#searchResults').innerHTML = data;
            })
            .catch(error => {
                // 서버에서 반환한 에러 메시지를 HTML에 삽입
                document.querySelector('#searchResults').innerHTML = `
            <div>
                <p>${error.message}</p>
            </div>
        `;
            });
    });
});
