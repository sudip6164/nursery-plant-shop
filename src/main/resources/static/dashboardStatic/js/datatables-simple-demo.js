window.addEventListener('DOMContentLoaded', event => {
    // Simple-DataTables


    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple);
    }
});
