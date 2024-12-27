<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="utf-8" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
                    <meta name="description" content="Hỏi Dân IT - Dự án laptopshop" />
                    <meta name="author" content="Hỏi Dân IT" />
                    <title>Order Table</title>
                    <link href="/css/styles.css" rel="stylesheet" />
                    <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js"
                        crossorigin="anonymous"></script>
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="../layout/header.jsp" />
                    <div id="layoutSidenav">
                        <jsp:include page="../layout/sidebar.jsp" />
                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <h1 class="mt-4">Manage Order</h1>
                                    <ol class="breadcrumb mb-4">
                                        <li class="breadcrumb-item"><a href="/admin">Dashboard</a></li>
                                        <li class="breadcrumb-item active">Order</li>
                                    </ol>
                                    <div>
                                        <div class="row">
                                            <div class="col-md-6 col-12 mx-auto">
                                                <h3>Update a order</h3>
                                                <hr />
                                                <form:form action="/admin/order/update" method="post"
                                                    modelAttribute="newOrder">
                                                    <div class="mb-3">
                                                        <p>Order id = ${newOrder.id}</p>
                                                        <p>Price:
                                                            <fmt:formatNumber type="number"
                                                                value="${newOrder.totalPrice}" />đ
                                                        </p>
                                                    </div>
                                                    <div class="mb-3" style="display: none;">
                                                        <label class="form-label">ID:</label>
                                                        <form:input path="id" type="text" class="form-control" />
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">User:</label>
                                                        <form:input path="user.fullName" type="text"
                                                            class="form-control" disabled="true" />
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Status:</label>
                                                        <form:select path="status" class="form-select"
                                                            aria-label="Default select example">
                                                            <form:option value="Apple">PENDING</form:option>
                                                            <form:option value="Asus">SHIPPING</form:option>
                                                            <form:option value="Lenovo">COMPLETE</form:option>
                                                            <form:option value="Dell">CANCEL</form:option>
                                                        </form:select>
                                                    </div>
                                                    <button type="submit" class="btn btn-warning">Update</button>
                                                </form:form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>
                            <jsp:include page="../layout/footer.jsp" />
                        </div>
                    </div>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
                        crossorigin="anonymous"></script>
                    <script src="/js/scripts.js"></script>
                </body>

                </html>