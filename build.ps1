# 强制设置控制台编码为UTF-8（解决中文乱码核心）
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 清空控制台
Clear-Host

# 可视化标题
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "          开始执行Maven编译脚本          " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 定义目标目录
$targetDir = "datastructure"
Write-Host "🔍 准备切换到目录：$targetDir" -ForegroundColor Yellow

# 1. 校验目录是否存在
if (-not (Test-Path -Path $targetDir -PathType Container)) {
    Write-Host "❌ 错误：目录 '$targetDir' 不存在！" -ForegroundColor Red
    Write-Host "请检查当前目录下是否有该文件夹" -ForegroundColor Red
    exit 1
}

# 2. 切换目录
try {
    Set-Location $targetDir
    Write-Host "✅ 成功切换到目录：$targetDir" -ForegroundColor Green
}
catch {
    Write-Host "❌ 切换目录失败：$($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 开始执行 mvn compile 命令..." -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host ""

# 3. 执行Maven编译（捕获所有输出，包括错误）
try {
    # 执行编译并实时输出日志（避免吞日志）
    mvn compile
    # 判断退出码
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host "✅ Maven 编译成功！" -ForegroundColor Green
        Write-Host "📌 编译产物在：$targetDir/target/classes 目录" -ForegroundColor Green
    } 
    else {
        Write-Host ""
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host "❌ Maven 编译失败！退出码：$LASTEXITCODE" -ForegroundColor Red
        Write-Host "💡 常见原因:pom.xml配置错误/代码语法错误/Maven未配置/依赖缺失" -ForegroundColor Yellow
    }
}
catch {
    Write-Host ""
    Write-Host "❌ 编译执行异常：$($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "            脚本执行结束                " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan