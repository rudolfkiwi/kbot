FROM --platform=$BUILDPLATFORM golang AS builder

ARG TARGETOS
ARG TARGETARCH

WORKDIR /go/src/app
COPY . .
RUN echo "TARGETOS=$TARGETOS TARGETARCH=$TARGETARCH" && \
    go version && \
    uname -m && \
    make build TARGETOS=$TARGETOS TARGETARCH=$TARGETARCH

FROM scratch
WORKDIR /
COPY --from=builder /go/src/app/kbot .
COPY --from=alpine:latest /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
ENTRYPOINT ["./kbot", "start"]
