package com.example.kingqi.paykeep;

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}