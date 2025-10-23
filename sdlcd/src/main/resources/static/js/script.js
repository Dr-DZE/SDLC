document.addEventListener('DOMContentLoaded', function () {
    console.log('DOM Content Loaded - script.js');
    const todoListContainer = document.getElementById('todo-list-container');

    if (todoListContainer) {
        // Accordion logic for todo items on the main page
        todoListContainer.addEventListener('click', function (event) {
            const target = event.target.closest('.todo-item');
            if (target && !event.target.closest('button')) {
                target.classList.toggle('expanded');
            }
        });
    }

    // Accordion logic for project tasks on the new page (using event delegation)
    document.body.addEventListener('click', function (event) {
        const projectContainer = event.target.closest('.project-container');
        if (projectContainer) {
            const target = event.target.closest('.todo-item');
            if (target && !target.classList.contains('locked') && !event.target.closest('.todo-actions button')) {
                target.classList.toggle('expanded');
            }
        }
    });

    // --- Modal Handling (using event delegation) --- //

    const openModal = (modal) => {
        if (modal) {
            modal.classList.add('visible');
            console.log('Modal opened:', modal.id, 'classes:', modal.classList);
        }
    };

    const closeModal = (modal) => {
        if (modal) modal.classList.remove('visible');
    };

    document.body.addEventListener('click', function (event) {
        const target = event.target;

        // --- Modal Opening Logic ---
        if (target.id === 'add-task-btn') {
            event.stopPropagation();
            openModal(document.getElementById('add-task-modal'));

        } else if (target.id === 'add-project-btn') {
            console.log('Add Project button clicked');
            event.stopPropagation();
            openModal(document.getElementById('add-project-modal'));

        } else if (target.classList.contains('add-branch-btn')) {
            console.log('Add Branch button clicked');
            event.stopPropagation();
            const projectId = target.getAttribute('data-project-id');
            const modal = document.getElementById(`add-branch-modal-${projectId}`);
            openModal(modal);

        } else if (target.classList.contains('add-task-to-track-btn')) {
            console.log('Add Task to Track button clicked');
            event.stopPropagation();
            const trackId = target.getAttribute('data-track-id');
            const modal = document.getElementById(`add-task-modal-${trackId}`);
            openModal(modal);

        // --- Modal Closing Logic ---
        } else if (target.classList.contains('close-button')) {
            event.stopPropagation();
            const modalToClose = target.closest('.modal-container');
            closeModal(modalToClose);

        } else {
            // Generic outside click handler to close any open modal
            const modals = document.querySelectorAll('.modal-container.visible');
            modals.forEach(modal => {
                // Check if the click was outside the modal content and not on a button that opens a modal
                const isModalTrigger = target.id.startsWith('add-') || target.classList.contains('add-branch-btn') || target.classList.contains('add-task-to-track-btn');
                if (!modal.querySelector('.modal-content').contains(target) && !isModalTrigger) {
                    closeModal(modal);
                }
            });
        }
    });

    // --- Form Submission Handling --- //

    function handleFormSubmit(form, successCallback) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new FormData(form);
            const action = form.getAttribute('action');
            const method = form.getAttribute('method');

            fetch(action, {
                method: method,
                body: new URLSearchParams(formData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                // Check if the response has content
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json();
                } else {
                    return {}; // Return empty object for no content
                }
            })
            .then(data => {
                if (successCallback) {
                    successCallback(data);
                }
                const modal = form.closest('.modal-container');
                if (modal) {
                    closeModal(modal);
                }
                form.reset();
            })
            .catch(error => {
                console.error('There has been a problem with your fetch operation:', error);
            });
        });
    }

    // --- Success Callbacks ---

    function addTaskSuccess(data) {
        const todoListContainer = document.getElementById('todo-list-container');
        if (todoListContainer) {
            const todoItem = document.createElement('div');
            todoItem.classList.add('todo-item', `difficulty-${data.difficulty.toLowerCase()}`);
            todoItem.setAttribute('data-id', data.id);

            todoItem.innerHTML = `
                <div class="todo-header">
                    <span class="todo-title">${data.title}</span>
                </div>
                <div class="todo-body">
                    <p>${data.details}</p>
                    <div>
                        <strong>Difficulty:</strong> <span>${data.difficulty}</span>
                    </div>
                    <div>
                        <strong>Due Date:</strong> <span>${new Date(data.dueDate).toLocaleDateString()}</span>
                    </div>
                    <div class="todo-actions">
                        <form>
                            <button type="button" class="btn-green toggle-todo-btn" data-todo-id="${data.id}">Toggle</button>
                        </form>
                        <form action="/delete/${data.id}" method="post">
                            <button type="submit" class="delete-btn">Delete</button>
                        </form>
                    </div>
                </div>
            `;
            todoListContainer.appendChild(todoItem);
        }
    }

    function addProjectSuccess(data) {
        const projectTabs = document.querySelector('.project-tabs ul');
        if (projectTabs) {
            const newTab = document.createElement('li');
            newTab.innerHTML = `<a href="/new-page?mode=dev&projectId=${data.id}">${data.name}</a>`;
            projectTabs.appendChild(newTab);
        }
    }

    function addBranchSuccess(data) {
        const tracksContainer = document.querySelector('.tracks-container');
        if (tracksContainer) {
            const newTrack = document.createElement('div');
            newTrack.classList.add('task-track', `theme-${data.colorTheme}`);
            newTrack.innerHTML = `
                <div class="track-header">
                    <h2>${data.name}</h2>
                    <div class="header-buttons">
                        <button class="add-task-to-track-btn plus-btn" data-track-id="${data.id}">+</button>
                        <form action="/track/delete/${data.id}" method="post">
                            <input type="hidden" name="mode" value="dev" />
                            <button type="submit" class="delete-btn small-delete-btn">X</button>
                        </form>
                    </div>
                </div>
                <div id="add-task-modal-${data.id}" class="modal-container">
                    <form action="/track/${data.id}/task/create" method="post" class="todo-form" data-ajax-form="true" data-success-callback="addTaskToTrackSuccess">
                        <span class="close-button">&times;</span>
                        <h2>Add New Task</h2>
                        <input type="text" name="title" placeholder="Task Title" required />
                        <textarea name="details" placeholder="Details" rows="3"></textarea>
                        <select name="difficulty" required>
                            <option value="" disabled selected>Select difficulty</option>
                            <option value="Easy">Easy</option>
                            <option value="Medium">Medium</option>
                            <option value="Hard">Hard</option>
                        </select>
                        <button type="submit">Add Task</button>
                    </form>
                </div>
                <div class="task-list"></div>
            `;
            tracksContainer.appendChild(newTrack);
        }
    }

    function addTaskToTrackSuccess(data) {
        const taskList = document.querySelector(`[data-track-id="${data.trackId}"]`).closest('.task-track').querySelector('.task-list');
        if (taskList) {
            const newTask = document.createElement('div');
            newTask.classList.add('todo-item');
            newTask.innerHTML = `
                <div class="todo-header">
                    <span class="todo-title">${data.title}</span>
                </div>
                <div class="todo-body">
                    <p>${data.details}</p>
                    <div class="todo-actions">
                        <form action="/project/task/update/${data.id}" method="post">
                            <input type="hidden" name="mode" value="dev" />
                            <button type="submit">Toggle</button>
                        </form>
                        <form action="/task/delete/${data.id}" method="post">
                            <input type="hidden" name="mode" value="dev" />
                            <button type="submit" class="delete-btn">Delete</button>
                        </form>
                    </div>
                </div>
            `;
            taskList.appendChild(newTask);
        }
    }


    // --- Attach form handlers ---
    const forms = document.querySelectorAll('form[data-ajax-form]');
    forms.forEach(form => {
        const callbackName = form.getAttribute('data-success-callback');
        const callback = window[callbackName];
        if (typeof callback === 'function') {
            handleFormSubmit(form, callback);
        }
    });

    // AJAX for toggling todo item completion
    document.querySelectorAll('.toggle-todo-btn').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            const todoId = this.dataset.todoId;
            const todoItemElement = this.closest('.todo-item');

            fetch(`/update/${todoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            })
            .then(response => response.json())
            .then(updatedTodo => {
                if (updatedTodo.completed) {
                    todoItemElement.classList.add('completed');
                } else {
                    todoItemElement.classList.remove('completed');
                }
            })
            .catch(error => console.error('Error toggling todo:', error));
        });
    });
});