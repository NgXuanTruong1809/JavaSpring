<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="utf-8" />
                <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
                <meta name="description" content="Hỏi Dân IT - Dự án laptopshop" />
                <meta name="author" content="Hỏi Dân IT" />
                <title>User Table</title>
                <link href="/css/styles.css" rel="stylesheet" />
                <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>

                <!-- Jquery for preview img -->
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
                <script>
                    $(document).ready(() => {
                        const avatarFile = $("#productFile");
                        avatarFile.change(function (e) {
                            const imgURL = URL.createObjectURL(e.target.files[0]);
                            $("#productPreview").attr("src", imgURL);
                            $("#productPreview").css({ "display": "block" });
                        });
                    });
                </script>

            </head>

            <body class="sb-nav-fixed">
                <jsp:include page="../layout/header.jsp" />
                <div id="layoutSidenav">
                    <jsp:include page="../layout/sidebar.jsp" />
                    <div id="layoutSidenav_content">
                        <main>
                            <div class="container-fluid px-4">
                                <h1 class="mt-4">Manage Products</h1>
                                <ol class="breadcrumb mb-4">
                                    <li class="breadcrumb-item"><a href="/admin">Dashboard</a></li>
                                    <li class="breadcrumb-item active">Products</li>
                                </ol>
                                <div class="">
                                    <div class="row">
                                        <div class="col-md-6 col-12 mx-auto">
                                            <h3>Create a product</h3>
                                            <hr />
                                            <form:form action="/admin/product/create" method="post"
                                                modelAttribute="newProduct" class="row" enctype="multipart/form-data">
                                                <!--enctype="multipart/form-data" for post file to server -->
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Name:</label>
                                                    <form:input path="name" type="text" class="form-control" />
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Price:</label>
                                                    <form:input path="price" type="number" step="0.1" value="0.0"
                                                        class="form-control" />
                                                </div>
                                                <div class="col-12 col-md-12 mb-3">
                                                    <label class="form-label">Detail description:</label>
                                                    <form:textarea path="detailDesc" type="text" class="form-control" />
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Short description:</label>
                                                    <form:input path="shortDesc" type="text" class="form-control" />
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Quantity:</label>
                                                    <form:input path="quantity" type="number" class="form-control"
                                                        value="0" />
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Factory:</label>
                                                    <form:select path="factory" class="form-select"
                                                        aria-label="Default select example">
                                                        <form:option value="Apple">Apple (MacBook)</form:option>
                                                        <form:option value="Asus">Asus</form:option>
                                                        <form:option value="Lenovo">Lenovo</form:option>
                                                        <form:option value="Dell">Dell</form:option>
                                                        <form:option value="LG">LG</form:option>
                                                        <form:option value="Acer">Acer</form:option>
                                                    </form:select>
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label">Target:</label>
                                                    <form:select path="target" class="form-select"
                                                        aria-label="Default select example">
                                                        <form:option value="Gaming">Gaming</form:option>
                                                        <form:option value="Study">Sinh viên - Văn phòng</form:option>
                                                        <form:option value="DesignWork">Thiết kế đồ họa</form:option>
                                                        <form:option value="Weight">Mỏng nhẹ</form:option>
                                                        <form:option value="Business">Doanh nhân</form:option>
                                                    </form:select>
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label for="avatarFile" class="form-label">Product image:</label>
                                                    <input class="form-control" type="file" id="productFile"
                                                        accept=".png, .jpg, .jpge" name="uploadFile" />
                                                </div>
                                                <div class="col-12 mb3">
                                                    <img alt="product preview" style="max-height: 250px; display: none;"
                                                        id="productPreview">
                                                </div>
                                                <div class="col-12 mb5">
                                                    <button type="submit" class="btn btn-primary">Create</button>
                                                </div>

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