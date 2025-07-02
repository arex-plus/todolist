// Todo列表管理的JavaScript代码

// API端点
const API_BASE_URL = '/api/todos';

// DOM元素
const todoList = document.getElementById('todoList');
const addTodoForm = document.getElementById('addTodoForm');
const editTodoModal = document.getElementById('editTodoModal');

// 获取所有Todo
async function fetchTodos() {
    try {
        const response = await fetch(API_BASE_URL, {
            headers: {}
        });
        const todos = await response.json();
        renderTodos(todos);
    } catch (error) {
        console.error('获取Todo列表失败:', error);
        alert('获取Todo列表失败');
    }
}

// 渲染Todo列表
function renderTodos(todos) {
    if (todos.length === 0) {
        todoList.innerHTML = '<div class="empty-state">暂无任务，添加一个新任务开始吧！</div>';
        return;
    }
    
    todoList.innerHTML = '';
    todos.forEach(todo => {
        const todoElement = document.createElement('div');
        todoElement.className = `todo-item ${todo.completed ? 'completed' : ''}`;
        
        // 创建复选框
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'checkbox';
        checkbox.checked = todo.completed;
        checkbox.addEventListener('change', () => toggleTodoStatus(todo.id, checkbox.checked));
        
        // 创建内容区域
        const todoContent = document.createElement('div');
        todoContent.className = 'todo-content';
        
        const todoTitle = document.createElement('div');
        todoTitle.className = 'todo-title';
        todoTitle.textContent = todo.title;
        
        const todoDescription = document.createElement('div');
        todoDescription.className = 'todo-description';
        todoDescription.textContent = todo.description || '';
        
        todoContent.appendChild(todoTitle);
        todoContent.appendChild(todoDescription);
        
        // 创建操作按钮区域
        const todoActions = document.createElement('div');
        todoActions.className = 'todo-actions';
        
        const editButton = document.createElement('button');
        editButton.className = 'btn-edit';
        editButton.textContent = '编辑';
        editButton.addEventListener('click', () => showEditTodoModal(todo.id, todo.title, todo.description || ''));
        
        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn-danger';
        deleteButton.textContent = '删除';
        deleteButton.addEventListener('click', () => deleteTodo(todo.id));
        
        todoActions.appendChild(editButton);
        todoActions.appendChild(deleteButton);
        
        // 组装元素
        todoElement.appendChild(checkbox);
        todoElement.appendChild(todoContent);
        todoElement.appendChild(todoActions);
        
        todoList.appendChild(todoElement);
    });
}

// 添加新Todo
addTodoForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const title = document.getElementById('todoTitle').value.trim();
    const description = document.getElementById('todoDescription').value.trim();

    if (!title) {
        alert('请输入任务标题');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `title=${encodeURIComponent(title)}&description=${encodeURIComponent(description)}`
        });

        if (response.ok) {
            addTodoForm.reset();
            fetchTodos();
        } else {
            throw new Error('添加Todo失败');
        }
    } catch (error) {
        console.error('添加Todo失败:', error);
        alert('添加Todo失败');
    }
});

// 切换Todo状态
async function toggleTodoStatus(id, completed) {
    try {
        const todo = await getTodoById(id);
        if (!todo) return;

        todo.completed = completed;
        const response = await fetch(`${API_BASE_URL}/update/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `title=${encodeURIComponent(todo.title)}&description=${encodeURIComponent(todo.description)}&completed=${todo.completed}`
        });

        if (!response.ok) {
            throw new Error('更新Todo状态失败');
        }

        fetchTodos();
    } catch (error) {
        console.error('更新Todo状态失败:', error);
        alert('更新Todo状态失败');
    }
}

// 获取单个Todo
async function getTodoById(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {});
        if (!response.ok) return null;
        return await response.json();
    } catch (error) {
        console.error('获取Todo详情失败:', error);
        return null;
    }
}

// 显示编辑Todo模态框
function showEditTodoModal(id, title, description) {
    document.getElementById('editTodoId').value = id;
    document.getElementById('editTodoTitle').value = title;
    document.getElementById('editTodoDescription').value = description;
    showEditModal();
}

// 保存编辑后的Todo
document.getElementById('saveTodoEdit').addEventListener('click', async () => {
    const id = document.getElementById('editTodoId').value;
    const title = document.getElementById('editTodoTitle').value.trim();
    const description = document.getElementById('editTodoDescription').value.trim();

    if (!title) {
        alert('请输入任务标题');
        return;
    }

    try {
        const todo = await getTodoById(id);
        if (!todo) return;

        todo.title = title;
        todo.description = description;

        const response = await fetch(`${API_BASE_URL}/update/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `title=${encodeURIComponent(todo.title)}&description=${encodeURIComponent(todo.description)}&completed=${todo.completed}`
        });

        if (response.ok) {
            closeEditModal();
            fetchTodos();
        } else {
            throw new Error('更新Todo失败');
        }
    } catch (error) {
        console.error('更新Todo失败:', error);
        alert('更新Todo失败');
    }
});

// 删除Todo
async function deleteTodo(id) {
    if (!confirm('确定要删除这个任务吗？')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/delete?id=${id}`, {
            method: 'POST'
        });

        if (response.ok) {
            fetchTodos();
        } else {
            throw new Error('删除Todo失败');
        }
    } catch (error) {
        console.error('删除Todo失败:', error);
        alert('删除Todo失败');
    }
}

// HTML转义函数
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 页面加载时获取Todo列表
document.addEventListener('DOMContentLoaded', fetchTodos);