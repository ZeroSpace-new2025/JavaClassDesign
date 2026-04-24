# 强制设置控制台编码为UTF-8（解决中文乱码）
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 清空控制台
Clear-Host

# 可视化标题
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "          开始执行Maven发布脚本          " -ForegroundColor Cyan
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
Write-Host "📦 第一步：清理历史编译/打包产物..." -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host ""

# 3. 清理历史产物（mvn clean）
try {
    mvn clean
    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "❌ 清理产物失败！退出码：$LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 清理历史产物完成" -ForegroundColor Green
}
catch {
    Write-Host ""
    Write-Host "❌ 清理产物异常：$($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 第二步：编译并打包项目（mvn package）..." -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host ""

# 4. 编译+打包（跳过测试可选：-DskipTests，避免测试失败导致打包中断）
try {
    # 如需跳过单元测试，将下方命令改为：mvn package -DskipTests
    mvn package
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host "✅ Maven 打包成功！" -ForegroundColor Green
        Write-Host "📌 打包产物路径：$targetDir/target/ （后缀通常为.jar）" -ForegroundColor Green
    } 
    else {
        Write-Host ""
        Write-Host "----------------------------------------" -ForegroundColor Gray
        Write-Host "❌ Maven 打包失败！退出码：$LASTEXITCODE" -ForegroundColor Red
        Write-Host "💡 常见原因：代码编译错误/测试用例失败/pom.xml配置错误" -ForegroundColor Yellow
        exit 1
    }
}
catch {
    Write-Host ""
    Write-Host "❌ 打包执行异常：$($_.Exception.Message)" -ForegroundColor Red
    exit 1
}


Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "            发布脚本执行结束            " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 切回脚本原始目录（避免后续操作路径混乱）
Set-Location -Path $PSScriptRoot