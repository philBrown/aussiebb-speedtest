FROM python:3.11-alpine

WORKDIR /app

RUN apk add build-base python3-dev

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY speed-test.py .

CMD ["python", "speed-test.py"]
