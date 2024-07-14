$(document).ready(function() {
    // Register
    $('#registerForm').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: 'register',
            type: 'POST',
            data: {
                username: $('#registerUsername').val(),
                password: $('#registerPassword').val()
            },
            success: function(response) {
                if (response.status === 'success') {
                    alert('Registration successful. Please login.');
                    $('#login-tab').tab('show');
                } else {
                    alert('Registration failed: ' + response.message);
                }
            },
            error: function() {
                alert('An error occurred during registration.');
            }
        });
    });

    // Login
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: 'login',
            type: 'POST',
            data: {
                username: $('#loginUsername').val(),
                password: $('#loginPassword').val()
            },
            success: function(response) {
                if (response.status === 'success') {
                    $('#loginRegisterForm').hide();
                    $('#scheduleManagement').show();
                    $('#username').text($('#loginUsername').val());
                } else {
                    alert('Login failed: ' + response.message);
                }
            },
            error: function() {
                alert('An error occurred during login.');
            }
        });
    });

    // Logout
    $('#logoutBtn').click(function() {
        $('#scheduleManagement').hide();
        $('#loginRegisterForm').show();
        $('#loginForm')[0].reset();
        $('#registerForm')[0].reset();
    });

    // Add Schedule
    $('#addScheduleForm').submit(function(e) {
        e.preventDefault();
        
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        
        // Validate date and time format
        var dateTimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/;
        if (!dateTimeRegex.test(startTime) || !dateTimeRegex.test(endTime)) {
            alert('Invalid date and time format. Please use YYYY-MM-DD HH:mm format.');
            return;
        }

        $.ajax({
            url: 'addSchedule',
            type: 'POST',
            data: {
                startTime: startTime,
                endTime: endTime,
                title: $('#title').val(),
                description: $('#description').val()
            },
            success: function(response) {
                if (response.status === 'success') {
                    alert('Schedule added successfully.');
                    $('#addScheduleForm')[0].reset();
                } else {
                    alert('Failed to add schedule: ' + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error: " + status + " - " + error);
                console.error("Response Text: " + xhr.responseText);
                alert('An error occurred while adding the schedule: ' + error);
            }
        });
    });

    // Query Schedules
    $('#queryScheduleForm').submit(function(e) {
        e.preventDefault();
        
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();
        
        // Validate date format
        var dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(startDate) || !dateRegex.test(endDate)) {
            alert('Invalid date format. Please use YYYY-MM-DD format.');
            return;
        }

        $.ajax({
            url: 'querySchedules',
            type: 'GET',
            data: {
                startDate: startDate,
                endDate: endDate
            },
            success: function(response) {
                if (response.status === 'error') {
                    alert('Failed to query schedules: ' + response.message);
                } else {
                    displaySchedules(response);
                }
            },
            error: function() {
                alert('An error occurred while querying schedules.');
            }
        });
    });

    // Clear All Schedules
    $('#clearSchedulesBtn').click(function() {
        if (confirm('Are you sure you want to clear all schedules?')) {
            $.ajax({
                url: 'clearSchedules',
                type: 'POST',
                success: function(response) {
                    if (response.status === 'success') {
                        alert('All schedules cleared successfully.');
                        $('#scheduleResults').empty();
                    } else {
                        alert('Failed to clear schedules: ' + response.message);
                    }
                },
                error: function() {
                    alert('An error occurred while clearing schedules.');
                }
            });
        }
    });

    function displaySchedules(schedules) {
        var scheduleHtml = '<h4>Schedules:</h4>';
        if (schedules.length === 0) {
            scheduleHtml += '<p>No schedules found for the selected date range.</p>';
        } else {
            scheduleHtml += '<ul class="list-group">';
            schedules.forEach(function(schedule) {
                scheduleHtml += '<li class="list-group-item">' +
                    '<h5>' + schedule.title + '</h5>' +
                    '<p>Start: ' + new Date(schedule.startTime).toLocaleString() + '</p>' +
                    '<p>End: ' + new Date(schedule.endTime).toLocaleString() + '</p>' +
                    '<p>' + schedule.description + '</p>' +
                    '<button class="btn btn-danger btn-sm delete-schedule" data-id="' + schedule.id + '">Delete</button>' +
                    '</li>';
            });
            scheduleHtml += '</ul>';
        }
        $('#scheduleResults').html(scheduleHtml);

        // Add event listener for delete buttons
        $('.delete-schedule').click(function() {
            var scheduleId = $(this).data('id');
            deleteSchedule(scheduleId);
        });
    }

    function deleteSchedule(scheduleId) {
        $.ajax({
            url: 'deleteSchedule',
            type: 'POST',
            data: {
                scheduleId: scheduleId
            },
            success: function(response) {
                if (response.status === 'success') {
                    alert('Schedule deleted successfully.');
                    $('#queryScheduleForm').submit(); // Refresh the schedule list
                } else {
                    alert('Failed to delete schedule: ' + response.message);
                }
            },
            error: function() {
                alert('An error occurred while deleting the schedule.');
            }
        });
    }
});