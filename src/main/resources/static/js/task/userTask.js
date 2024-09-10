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
                newCommContainer.dataset.commentId = data.newComment.id; // data-comment-id 속성 설정

                // 댓글 컨테이너
                const contentWrapper = document.createElement('div');
                contentWrapper.className = 'writterContainer';

                // 작성자 이름
                const authorSpan = document.createElement('span');
                authorSpan.className = 'comment-author';
                contentWrapper.appendChild(authorSpan);

                if (data.newComment.anonymous) {
                    authorSpan.textContent = '익명';
                } else {
                    authorSpan.textContent = data.newComment.userName; // 서버에서 user_name을 받아옴

                    // 만약 comm.user_id가 task.userId와 같으면 " (제출자)" 추가
                    if (userId === taskUserId) {
                        const submitterSpan = document.createElement('span');
                        submitterSpan.className = 'comment-author2';
                        submitterSpan.textContent = ' (제출자)';
                        contentWrapper.appendChild(submitterSpan);
                    }
                }

                // 내용
                const commentSpan = document.createElement('span');
                commentSpan.className = 'comment-content';
                commentSpan.textContent = data.newComment.contents;

                // 시간 (commentTime)
                const commentTimeSpan = document.createElement('span');
                commentTimeSpan.className = 'commentTime';
                commentTimeSpan.textContent = data.newComment.updatedDate;

                // 댓글 삭제 여부
                const deleteWrapper = document.createElement('div');
                deleteWrapper.className = 'delete';
                deleteWrapper.dataset.commentId = data.newComment.id; // 삭제 시 사용할 commentId 설정

                // 로그인한 사용자와 댓글 작성자의 user_id를 비교하여 삭제 버튼 생성
                if (data.newComment.user_id === userId) {
                    const deleteButton = document.createElement('span');
                    deleteButton.id = 'deleteCommentButton';
                    deleteButton.className = 'comment-delete';
                    deleteButton.textContent = '✘';

                    // 삭제 버튼에 이벤트 추가 (이벤트 리스너를 통해 댓글 삭제 기능 구현)
                    deleteButton.addEventListener('click', function() {
                        // 삭제 로직 처리
                        deleteComment(data.newComment.id);
                    });

                    deleteWrapper.appendChild(deleteButton);
                }

                // 작성자와 댓글 내용을 포함하는 컨테이너에 추가
                contentWrapper.appendChild(commentSpan);

                // 댓글 컨테이너에 추가
                newCommContainer.appendChild(contentWrapper);
                newCommContainer.appendChild(commentTimeSpan);
                newCommContainer.appendChild(deleteWrapper);

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

    /* 댓글 삭제 */
    document.addEventListener('click', function(event) {
        // 삭제 버튼을 눌렀을 때만 처리
        const deleteCommentButton = event.target.closest('#deleteCommentButton');

        if (deleteCommentButton) {
            // 해당 댓글의 ID (commentId)를 추출 (부모 요소의 data-comment-id 속성 사용)
            const commentBox = deleteCommentButton.closest('.comment-box'); // 댓글 전체를 포함하는 .comment-box 요소 찾기
            const commentId = commentBox.dataset.commentId; // 여기서 commentId 추출
            console.log('삭제할 댓글 ID:', commentId);

            // 서버로 DELETE 요청 전송
            if (commentId) {
                fetch(`/groups/userTask/${commentId}/deleteComment`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                })
                    .then(response => {
                        if (response.ok) {
                            // 댓글이 성공적으로 삭제되면 뷰에서 해당 댓글 요소 제거
                            commentBox.remove();
                        } else {
                            alert('댓글 삭제에 실패했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('Error deleting comment:', error);
                        alert('댓글 삭제 중 오류가 발생했습니다.');
                    });
            }
        }
    });

});
