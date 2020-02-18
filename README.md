# MibandApp2_db_up


miband2에서 측정된 심박수 데이터를 local의 MariaDB에 저장하는 애플리케이션

## android - php - mariaDB
(추후 Firebase로 변경)

1. miband2는 다른 웨어러블 디바이스 제작사처럼 공식 SDK가 없어 인터넷에 있는 코드를 참고하였습니다.
2. 데이터 통신(블루투스)을 위해서 심박수, 걸음수, 배터리 잔량 등 각 기능에 맞는 UUID를 이용하였습니다.
3. php서버는 xampp를 사용하였고 apache와 mariaDB(mysql대신)을 사용하였습니다.
4. 추후 다른 서비스로의 확장을 위해 애플리케이션은 기본 기능만 구현하였습니다.
