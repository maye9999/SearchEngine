<%--
  Created by IntelliJ IDEA.
  User: MaYe
  Date: 2016/6/13
  Time: 21:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- 新 Bootstrap 核心 CSS 文件 -->
        <link rel="stylesheet" href="/css/bootstrap.min.css">

        <!-- font-awesome -->
        <link rel="stylesheet" href="/css/font-awesome.min.css">

        <link rel="stylesheet" href="/css/index.css">

        <title>THU Search</title>

    </head>
    <body>
        <div class="site-wrapper">
            <div class="site-wrapper-inner">
                <div class="site-container">
                    <div class="logo-index">
                        <img src="/image/logo.png">
                    </div>
                    <div>
                        <h1>ThuThuGo</h1>
                        <form action="result" method="get">
                            <div class="input-group">
                                <input type="text" class="form-control input-lg" name="query" placeholder="Search for ...">
                                <input type="text" name="page" value="0" style="display: none;">
                                <span class="input-group-btn">
                                    <button class="btn btn-lg btn-default" type="submit"><i class="fa fa-search"></i></button>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
        <script src="/js/jquery.min.js"></script>

        <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
        <script src="/js/bootstrap.min.js"></script>
    </body>
</html>
