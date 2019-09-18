package com.example.android.codelabs.paging.data

import com.example.android.codelabs.paging.model.Repo
import com.google.gson.Gson

val flutterRepo = Gson().fromJson("{\n" +
        "      \"id\":31792824,\n" +
        "      \"name\":\"flutter\",\n" +
        "      \"full_name\":\"flutter/flutter\",\n" +
        "      \"description\":\"Flutter makes it easy and fast to build beautiful mobile apps.\",\n" +
        "      \"html_url\":\"https://github.com/flutter/flutter\",\n" +
        "      \"stargazers_count\":74403,\n" +
        "      \"forks_count\":9130,\n" +
        "      \"language\":\"Dart\" }", Repo::class.java)


val materialDesignIconsRepo = Gson().fromJson("{\n" +
        "      \"id\":26373281,\n" +
        "      \"name\":\"material-design-icons\",\n" +
        "      \"full_name\":\"google/material-design-icons\",\n" +
        "      \"description\":\"Material Design icons by Google\",\n" +
        "      \"html_url\":\"https://github.com/google/material-design-icons\",\n" +
        "      \"stargazers_count\":38772,\n" +
        "      \"forks_count\":7952,\n" +
        "      \"language\":\"CSS\" }", Repo::class.java)
