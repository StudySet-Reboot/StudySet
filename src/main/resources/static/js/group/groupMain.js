document.addEventListener("DOMContentLoaded", function() {
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
});
