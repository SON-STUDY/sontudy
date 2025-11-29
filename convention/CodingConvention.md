# 코딩 컨벤션 (Coding Convention)

본 문서는 프로젝트 전반에 걸쳐 일관된 코드 스타일을 유지하기 위한 코딩 컨벤션을 정의합니다.

---

## 1. 네이밍 규칙 (Naming Rules)

자바(Java) 언어의 표준 네이밍 컨벤션을 따릅니다.

*   **클래스 및 인터페이스**: 파스칼 케이스 (PascalCase), 명사 형태 사용 (예: `UserService`)
*   **메서드 및 변수**: 카멜 케이스 (camelCase), 메서드는 동사로 시작 (예: `getUserProfile()`, `userName`)
*   **상수 (final static)**: 스네이크 케이스 (SNAKE_CASE), 모두 대문자 사용 (예: `MAX_VALUE`)
*   **패키지**: 모두 소문자 사용 (예: `com.example.project`)

## 2. 들여쓰기 (Indentation)

**탭 문자 대신 4개의 공백(4 spaces)을 사용**하여 들여쓰기를 수행합니다.

## 3. 반환 (Return)

함수(메서드)는 특정 값을 반환해야 하는 경우, **함수의 맨 마지막에서 한 번만 반환**하는 것을 원칙으로 합니다. 단, 예외 처리(Early Return)를 위해 함수 중간에 빠져나가는 경우는 예외로 합니다.

*   `return` 문 바로 위에는 가독성을 위해 한 줄을 비워 놓습니다.

```
// 권장 사례
public int calculate(int value) {
    if (value < 0) {
        return 0; // 예외 처리 (Early Return)
    }

    // ... 로직 처리 ...

    int result = value * 10;

    // 반환 전 한 줄 공백
    return result;
}

// 비권장 사례
public int calculate(int value) {
    if (value >= 0) {
        // ...
        return result;
    }
    return 0;
}
```


## 4. 괄호 (Parentheses)
if, for, while 등 제어문 사용 시, **키워드와 괄호 사이에 공백**을 둡니다. 블록 시작 괄호({)는 같은 줄에 위치시킵니다.
```
// 권장 사례
if (condition) {
    // ...
} else if (anotherCondition) {
    // ...
} else {
    // ...
}

// 비권장 사례
if(condition){
    // ...
}
```

## 5. 연산자 (Operators)
가독성을 높이기 위해 *모든 이항 연산자(+, -, , /, =, ==, >, <, &&, || 등) 전후로 공백을 둡니다.
```
// 권장 사례
value = (a + b) * c;
if (x == y || z != 0) {
    // ...
}

// 비권장 사례
value=(a+b)*c;
if (x==y||z!=0) {
    // ...
}
```

## 6. 주석 (Comments)
코드의 이해나 정보 전달에 꼭 필요한 경우가 아니면 주석 사용을 지양합니다. 코드를 명확하게 작성하여 주석 없이도 이해할 수 있도록 노력합니다.
코드를 이해하기 어렵게 만드는 복잡한 로직이나, 중요한 비즈니스 규칙을 설명할 때만 사용합니다.
불필요한 주석, 히스토리 주석, 주석 처리된 코드는 제거합니다.



