import { displayToast, displayErrorToast } from '../common/toast.js';
document.addEventListener("DOMContentLoaded", function() {
    $(document).ready(function() {
        var groupId = document.querySelector('#groupId').value;
        $('#calendar').fullCalendar({
            initialView: 'dayGridWeek',
            locale: 'ko',
            header: {
                center: 'title',
                left: 'none',
                right: 'none'
            },
            showNonCurrentDates: false,
            events: function(start, end, timezone, callback) {
                const url = `/api/groups/${groupId}/schedules/events`
                fetch(url, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        callback(data);
                    })
                    .catch(error => {
                        console.error('Error fetching events:', error);
                        callback([]);
                    });
            },
            height: 'auto',
            eventRender: function(event, element) {
                element.find('.fc-time').remove();
            }
        });
    });

    // 현재 날짜 출력
    const today = new Date();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(today.getDate()).padStart(2, '0');
    const dateString = month + '/' + day;
    document.getElementById('currentDate').textContent = dateString;

    // 그룹 코드 확인
    const toast = document.getElementById("toast");
    const btn = document.getElementById("groupCodeBtn");
    const groupCodeDisplay = document.getElementById("groupCodeDisplay");
    const toastText = document.getElementById("toastText");

    btn.onclick = function(event) {
        event.preventDefault();

        // 그룹 코드 값을 읽어오기
        const groupCode = groupCodeDisplay.getAttribute("data-group-code");
        displayToast(`그룹코드: ${groupCode}`);
        toast.className = "toast show";
    };

    // 그룹원 검색
    const searchBtn = document.getElementById("searchMemberBtn");
    const searchModal = document.getElementById("searchModal");

    searchBtn.onclick = function() {
        searchModal.style.display = "block";
    }

    window.onclick = function(event) {
        if (event.target == searchModal) {
            searchModal.style.display = "none";
        }
    }

    // 그룹원 검색 키워드 제출
    const searchModalForm = document.getElementById("searchModalForm");
    searchModalForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼의 기본 제출 동작 방지

        // 폼 데이터 가져오기
        const form = event.target;
        const keyword = form.querySelector("#keyword").value;
        const groupId = form.querySelector("#groupId").value;

        // URL 쿼리 문자열 만들기
        const url = new URL(form.action, window.location.origin);
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
    const leaveGroupModal = document.getElementById('leaveGroupModal');
    const leaveGroupForm = document.getElementById('leaveGroupForm');

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
            const leaveGroupModal = document.getElementById('leaveGroupModal');
            leaveGroupModal.style.display = "none";
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
                    displayErrorToast('그룹 탈퇴가 완료되었습니다.');
                    leaveGroupModal.style.display = "none";

                    setTimeout(() => {
                        window.location.href = '/users/main';
                    }, 1000);
                } else {
                    return response.json().then(err => {
                        throw new Error(err.message);
                    });
                }
            })
            .catch(error => {
                displayErrorToast(error.message);
            });
    });
});