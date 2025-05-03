output "vpc_id" {
  description = "생성된 VPC의 ID"
  value       = aws_vpc.main.id
}

output "vpc_cidr_block" {
  description = "VPC의 CIDR 블록"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "생성된 퍼블릭 서브넷 ID 리스트"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "생성된 프라이빗 서브넷 ID 리스트"
  value       = aws_subnet.private[*].id
}

output "dev_private_subnet_ids" {
  description = "개발 환경 프라이빗 서브넷 ID 리스트"
  value       = length(aws_subnet.private) > 0 ? [aws_subnet.private[0].id] : []
}

output "release_private_subnet_ids" {
  description = "릴리스 환경 프라이빗 서브넷 ID 리스트"
  value       = length(aws_subnet.private) > 1 ? [aws_subnet.private[1].id] : []
}

output "nat_gateway_ids" {
  description = "생성된 NAT 게이트웨이 ID 리스트"
  value       = aws_nat_gateway.main[*].id
}

output "internet_gateway_id" {
  description = "생성된 인터넷 게이트웨이 ID"
  value       = aws_internet_gateway.main.id
}
