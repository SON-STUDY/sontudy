# Git & GitHub 컨벤션 가이드라인

본 문서는 프로젝트의 효율적인 협업과 코드 이력 관리를 위해 팀이 준수해야 할 Git 전략, 브랜치 규칙 및 커밋 메시지 컨벤션을 명시합니다.

## 1. Commit Message Convention (커밋 메시지 규칙)

커밋 메시지는 **`type(소문자): 커밋 메시지 내용`** 형식으로 작성합니다.

```
type: 커밋 메시지 내용
예시: feat: Add user login functionality

Type 목록
Type	설명
feat	새로운 기능 추가 (A new feature)
fix	버그 수정 (A bug fix)
docs	문서 관련 수정 (예: README, 가이드 문서)
test	테스트 코드 추가 또는 수정 (예: 단위 테스트, 통합 테스트)
chore	기타 자잘한 변경 (예: 설정 파일, 빌드 작업 업데이트, 패키지 설치)
refactor  코드 리팩토링 (기능 변경 없이 코드 구조/가독성 개선)
delete	파일 또는 코드 삭제
rename	파일 또는 디렉토리 이름 변경
```

## 2. Git Branch Strategy (Git 브랜치 전략)
   Git Flow 전략을 기반으로 브랜치를 운영하며, 주요 브랜치는 다음과 같습니다.
  
 브랜치 역할
  - main:	프로덕션 환경에 배포되는 안정적인 최신 코드 (배포 전용)
  - develop:	다음 배포 버전을 준비하기 위한 통합 브랜치 (개발 메인)
- feat:	기능 개발 브랜치 (develop에서 분기)
- hotfix:	main 브랜치의 치명적인 버그 긴급 수정 (main에서 분기)

## 3. Branch Naming Rules (브랜치 네이밍 규칙)
브랜치는 type/#이슈번호-기능설명 형식으로 명확하게 작성합니다.
   - 이슈 트래커(예: GitHub Issues)와 연동하여 관리합니다.
   - 기능 설명 부분은 하이픈(-)으로 연결하며, 영문 소문자를 사용합니다.
```
type/#이슈번호-기능설명
예시: feat/#100-user-domain
예시: fix/#20-login-error
```

## 4. Commit Frequency (커밋 빈도 및 단위)
- 커밋은 자주 수행합니다.
- 작은 기능 구현 완료, 버그 수정 완료 등 논리적으로 의미 있는 하나의 완성된 단위로 커밋합니다.
- 커밋 하나가 독립적인 의미를 가지도록 하여, 추후 코드 이력 추적이나 문제 발생 시 Rollback이 용이하도록 합니다.