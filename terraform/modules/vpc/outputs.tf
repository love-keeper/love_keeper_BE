output "vpc_id" {
  description = "The ID of the VPC"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "The CIDR block of the VPC"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_1_id" {
  description = "The ID of public subnet 1"
  value       = aws_subnet.public_1.id
}

output "public_subnet_2_id" {
  description = "The ID of public subnet 2"
  value       = aws_subnet.public_2.id
}

output "private_subnet_1_id" {
  description = "The ID of private subnet 1 (Dev)"
  value       = aws_subnet.private_1.id
}

output "private_subnet_2_id" {
  description = "The ID of private subnet 2 (Prod)"
  value       = aws_subnet.private_2.id
}

output "nat_gateway_1_id" {
  description = "The ID of NAT Gateway 1"
  value       = aws_nat_gateway.nat_1.id
}

output "nat_gateway_2_id" {
  description = "The ID of NAT Gateway 2"
  value       = aws_nat_gateway.nat_2.id
}

output "internet_gateway_id" {
  description = "The ID of the Internet Gateway"
  value       = aws_internet_gateway.main.id
}