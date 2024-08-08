document.addEventListener("DOMContentLoaded", function() {
    function scrollToBottom(element) {
        element.scrollTop = element.scrollHeight;
    }

    async function addMemo(buttonElement) {
        // userId와 groupId 갖고 옴
        const userFrame = buttonElement.closest('.user-frame');
        const userId = userFrame.getAttribute('data-user-id');
        const groupId = userFrame.getAttribute('data-group-id');
        const memoInput = buttonElement.closest('.memo-container').querySelector('.memo-input');
        const memoContent = memoInput.value.trim();

        if (memoContent === '') {
            alert('메모를 입력해 주세요.');
            return;
        }

        // 메모를 서버에 전송
        try {
            const response = await fetch('/groups/memo/addMemo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    groupId: groupId,
                    content: memoContent
                })
            });

            if (response.ok) {
                const data = await response.json();

                // 페이지 업데이트
                const memoWrapper = userFrame.querySelector('.memo-wrapper');
                const noteContainers = userFrame.querySelectorAll('.note');

                // 새 메모를 추가
                let targetNoteContainer;
                if (noteContainers.length > 0) {
                    targetNoteContainer = noteContainers[noteContainers.length - 1];
                } else {
                    targetNoteContainer = userFrame; // .note 요소가 없는 경우 userFrame에 직접 추가
                }

                const newNoteContainer = document.createElement('div');
                newNoteContainer.className = 'note';

                const newMemo = document.createElement('div');
                newMemo.className = 'memo';
                newMemo.textContent = data.newMemo.contents;

                newNoteContainer.appendChild(newMemo);

                if (targetNoteContainer.nextSibling) {
                    targetNoteContainer.parentNode.insertBefore(newNoteContainer, targetNoteContainer.nextSibling);
                } else {
                    targetNoteContainer.parentNode.appendChild(newNoteContainer);
                }

                // 입력창 초기화
                memoInput.value = '';

                // 스크롤을 맨 아래로 이동
                scrollToBottom(memoWrapper);
            } else {
                alert('메모 전송에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('메모 전송 중 오류가 발생했습니다.');
        }
    }
    // 페이지 로드 시 스크롤 맨 아래로
    document.querySelectorAll('.user-frame').forEach(userFrame => {
        const memoWrapper = userFrame.querySelector('.memo-wrapper');
        if (memoWrapper) {
            scrollToBottom(memoWrapper);
        }
    });

    document.querySelectorAll('.add-note').forEach(button => {
        button.addEventListener('click', function() {
            addMemo(this);
        });
    });
});
