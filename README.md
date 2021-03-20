# 개발 정리 노트



### 엔티티 설계 주의점

1. 가급적 `Setter` 사용x

   - 변경 포인터가 너무 많아져서 유지보수가 어려워짐

2. **모든 연관관계는 지연로딩으로 설정**

   - 실무에서는 모든 연관관계를 `LAZY`(지연로딩)로 설정해야함!!!!⭐⭐⭐ <- 매우 중요

     =>  `EAGER`(즉시로딩)은 예측이 어려워 어떤 SQL이 실행될지 알 수 X

3. 연관된 엔티티를 DB에서 함께 조인하고 싶을 때

   1. `fetch join` 사용

   2. 엔티티 그래프 기능 사용













