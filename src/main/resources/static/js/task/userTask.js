document.addEventListener('DOMContentLoaded', function() {
    const deleteButton = document.getElementById('deleteTaskButton');

    if (deleteButton) {
        deleteButton.addEventListener('click', function(event) {
            event.preventDefault();

            // 사용자에게 삭제 확인 메시지 표시
            const confirmation = confirm("정말로 이 과제를 삭제하시겠습니까?");

            if (confirmation) {
                // 사용자가 확인을 클릭한 경우에만 삭제 요청을 보냄
                fetch(`/groups/task/${taskId}/${userId}/deleteTask`, {
                    method: 'DELETE'
                })
                    .then(response => {
                        if (response.ok) {
                            // 삭제 성공 시 리다이렉트
                            alert("과제를 삭제하였습니다.");
                            window.location.href = `/groups/${groupId}/${taskId}/taskDetail`;
                        } else {
                            // 오류 처리
                            return response.text().then(text => {
                                throw new Error(text);
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('과제 삭제에 실패했습니다: ' + error.message);
                    });
            } else {
                // 사용자가 취소를 클릭한 경우 아무 작업도 하지 않음
                alert("과제 삭제를 취소하였습니다.");
            }
        });
    }

    /* 댓글 전송 */
    async function addComment(buttonElement){
        const userFrame = buttonElement.closest('.comment-container');
        const commentInput = buttonElement.closest('.comment-form').querySelector('.writeComm');
        const commContent = commentInput.value.trim();
        const checkbox = buttonElement.closest('.comment-form').querySelector('input[name="anonymous"]');
        const isChecked = checkbox.checked;

        if (commContent === '') {
            alert('댓글을 작성해 주세요.');
            return;
        }

        // 댓글을 서버에 전송
        try {
            const response = await fetch('/groups/userTask/addComment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    submission_id: submission_id,
                    contents: commContent,
                    anonymous: isChecked
                })
            });

            if (response.ok) {
                const data = await response.json();

                // userFrame 내의 .comments를 찾거나 생성
                let comments = userFrame.querySelector('.comments');
                if (!comments) {
                    comments = document.createElement('div');
                    comments.className = 'comments';
                    userFrame.insertBefore(comments, userFrame.querySelector('.comment-form')); // 댓글 입력창 위에 삽입
                }

                // 새 댓글 추가
                const newCommContainer = document.createElement('div');
                newCommContainer.className = 'comment-box';

                // 댓글 컨테이너
                const contentWrapper = document.createElement('div');
                contentWrapper.className = 'writterContainer';

                // 작성자 이름
                const authorSpan = document.createElement('span');
                authorSpan.className = 'comment-author';
                if (data.newComment.anonymous) {
                    authorSpan.textContent = '익명';
                } else {
                    authorSpan.textContent = data.newComment.user.name;
                }

                // 내용
                const commentSpan = document.createElement('span');
                commentSpan.className = 'comment-content';
                commentSpan.textContent = data.newComment.contents;

                // 시간 (commentTime)
                const commentTimeSpan = document.createElement('span');
                commentTimeSpan.className = 'commentTime';
                commentTimeSpan.textContent = data.newComment.updatedDate;

                // 작성자와 댓글 내용을 포함하는 컨테이너에 추가
                contentWrapper.appendChild(authorSpan);
                contentWrapper.appendChild(commentSpan);

                // 댓글 컨테이너에 추가
                newCommContainer.appendChild(contentWrapper);
                newCommContainer.appendChild(commentTimeSpan);

                // 댓글 리스트에 새 댓글 추가
                comments.appendChild(newCommContainer);

                // 입력창 초기화
                commentInput.value = '';
            }
            else {
                alert('댓글 전송에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('댓글 전송 중 오류가 발생했습니다.');
        }
    }

    document.querySelectorAll('.add-comment').forEach(button => {
        button.addEventListener('click', function() {
            addComment(this);
        });
    });
});
