document.addEventListener("DOMContentLoaded", function() {
    async function addMemo(buttonElement) {
        // 버튼의 부모 요소에서 userId와 groupId를 가져옵니다
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
            const response = await fetch('/memo/addMemo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                    // CSRF 토큰이 비활성화되어 있으므로 생략
                },
                body: JSON.stringify({
                    userId: userId,
                    groupId: groupId,
                    content: memoContent
                })
            });

            if (response.ok) {
                const data = await response.json();

                // 응답이 성공적이라면, 페이지를 업데이트합니다
                const noteContainers = userFrame.querySelectorAll('.note');

                // 새 메모를 추가할 위치를 선택
                let targetNoteContainer;
                if (noteContainers.length > 0) {
                    targetNoteContainer = noteContainers[noteContainers.length - 1];
                } else {
                    targetNoteContainer = userFrame; // .note 요소가 없는 경우 userFrame에 직접 추가
                }

                // 새로운 note 요소를 생성
                const newNoteContainer = document.createElement('div');
                newNoteContainer.className = 'note';

                // 새로운 memo 요소를 생성
                const newMemo = document.createElement('div');
                newMemo.className = 'memo';
                newMemo.textContent = data.newMemo.contents;

                // note 요소에 memo 요소를 추가
                newNoteContainer.appendChild(newMemo);

                // targetNoteContainer의 뒤에 새로운 note를 추가
                if (targetNoteContainer.nextSibling) {
                    targetNoteContainer.parentNode.insertBefore(newNoteContainer, targetNoteContainer.nextSibling);
                } else {
                    targetNoteContainer.parentNode.appendChild(newNoteContainer);
                }

                // 입력창을 초기화합니다
                memoInput.value = '';
            } else {
                alert('메모 전송에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('메모 전송 중 오류가 발생했습니다.');
        }
    }

    document.querySelectorAll('.add-note').forEach(button => {
        button.addEventListener('click', function() {
            addMemo(this);
        });
    });
});
