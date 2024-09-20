document.addEventListener("DOMContentLoaded", function() {
    // 현재 날짜 출력
    const today = new Date();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(today.getDate()).padStart(2, '0');
    const dateString = month + '/' + day;
    document.getElementById('currentDate').textContent = dateString;

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
            searchModal.style.display = "none";
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

    // 그룹 탈퇴
    const groupLeaveBtn = document.getElementById('groupLeaveBtn');
    const leaveGroupModal = document.getElementById('leaveGroupModal');
    const closeBtn = document.querySelector('.close-btn2'); // 여러 개의 닫기 버튼을 선택
    const confirmationStep = document.getElementById('confirmationStep');
    const codeStep = document.getElementById('codeStep');
    const leaveGroupForm = document.getElementById('leaveGroupForm');

    // 모달 열기 함수
    function openLeaveGroupModal() {
        leaveGroupModal.style.display = "block";
    }

    // 모달 닫기 함수
    function closeLeaveGroupModal() {
        closeBtn.parentElement.parentElement.style.display = "none";
    }

    // 그룹 탈퇴 버튼 클릭 시 모달 열기
    groupLeaveBtn.addEventListener('click', function(event) {
        openLeaveGroupModal();
    });

    // 모든 닫기 버튼 클릭 시 모달 닫기
    if (closeBtn) { // closeBtn이 null이 아닐 때만 이벤트 추가
        closeBtn.addEventListener('click', function () {
            closeLeaveGroupModal();
        });
    }

    // 모달 외부 클릭 시 모달 닫기
    window.addEventListener('click', function(event) {
        if (event.target === leaveGroupModal) {
            closeLeaveGroupModal();
        }
    });

    // 확인 버튼 클릭 시 코드 입력 단계로 이동
    const confirmLeaveBtn = document.getElementById('confirmLeaveBtn');
    if (confirmLeaveBtn) {
        confirmLeaveBtn.addEventListener('click', function() {
            confirmationStep.style.display = "none"; // 확인 단계 숨기기
            codeStep.style.display = "block"; // 코드 입력 단계 표시하기
        });
    }

    // 취소 버튼 클릭 시 모달 닫기
    const cancelLeaveBtn = document.getElementById('cancelLeaveBtn');
    if (cancelLeaveBtn) {
        cancelLeaveBtn.addEventListener('click', function() {
            closeLeaveGroupModal();
        });
    }

    // 그룹 코드 입력 취소 버튼 클릭 시 단계 전환
    const cancelCodeBtn = document.getElementById('cancelCodeBtn');
    if (cancelCodeBtn) {
        cancelCodeBtn.addEventListener('click', function() {
            codeStep.style.display = "none"; // 코드 입력 단계 숨기기
            confirmationStep.style.display = "block"; // 확인 단계 표시하기
        });
    }

    // 폼 제출 시 서버로 요청 보내기
    leaveGroupForm.addEventListener('submit', function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 방지

        const groupCode = document.getElementById('groupCode').value;

        fetch('/groups/leave', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ code: groupCode })
        })
            .then(response => {
                if (response.ok) {
                    if (response.status === 200) {
                        alert('그룹 탈퇴가 완료되었습니다.');
                        closeLeaveGroupModal();
                        window.location.href = '/users/main';
                    } else {
                        alert("알 수 없는 응답 상태 코드: " + response.status);
                    }
                } else {
                    return response.json().then(err => {
                        throw new Error(err.message);
                    });
                }
            })
            .catch(error => {
                alert(`${error.message}`);
            });
    });
});
