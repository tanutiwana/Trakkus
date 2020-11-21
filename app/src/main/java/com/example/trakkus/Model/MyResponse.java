package com.example.trakkus.Model;

import java.util.List;

import javax.xml.transform.Result;

public class MyResponse {
    public long multicase_id;

    public int success,failure, canonical_ids;
    public List<Result> result;
}
