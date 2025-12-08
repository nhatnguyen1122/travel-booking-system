document.addEventListener('DOMContentLoaded', () => {
    const videoSlider = document.getElementById('video-slider');
    const controlButtons = document.querySelectorAll('.vid-btn');

    controlButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons
            controlButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to the clicked button
            button.classList.add('active');
            // Change video source
            const newSrc = button.getAttribute('data-src');
            videoSlider.src = newSrc;
            // Load and play the new video
            videoSlider.load();
            videoSlider.play();
        });
    });
});
